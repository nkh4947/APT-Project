package org.tizen.common.annotation.apt.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.tizen.common.annotation.apt.data.SourceType;
import org.tizen.common.annotation.apt.template.Boilerplate;
import org.tizen.common.annotation.apt.util.CreateSource;
import org.tizen.common.annotation.apt.util.CreateSourceDialog;
import org.tizen.common.annotation.apt.util.Logger;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.TypeDeclaration;

public class CreateSourceAnnotationProcessor extends FileCreatingAbstractAnnotationProcessor {

    public static final String CREATE_SOURCE_QUALIFIED_NAME = "org.tizen.common.annotation.CreateSource";
    private static final String DEFAULT_AUTHOR = "Ho Namkoong";
    private static final String DEFAULT_ACCOUNT = "ho.namkoong@samsung.com";
    private ArrayList<MappingUnit> mirrorList = new ArrayList<MappingUnit>();
    
    private static final String[] ANNOTATION_TAGS = {
        "sourceName",
        "author",
        "account",
        "pack",
        "sourceType"
    };
    
    public CreateSourceAnnotationProcessor(AnnotationProcessorEnvironment env) {
        super(env, ANNOTATION_TAGS);
    }

    @Override
    public void process() {
        
        makeAnnotationMap();
        createBlockFiles();
        
        for(Map.Entry<File, List<MappingUnit>> entry: this.mapper.entrySet()) {
            this.mirrorList.clear();
            File sourceFile = entry.getKey();
            List<MappingUnit> value = entry.getValue();
            
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(sourceFile.getAbsolutePath()));
            
            if(!this.checkFileValidation(file)) {
                continue;
            }
            StringBuffer buffer = new StringBuffer();
            Collections.sort(value, comparator);
            
            this.preAppendBuffer(buffer, value);
            if(!this.appendBuffer(buffer, sourceFile, value)) {
                continue;
            }
            this.postAppendBuffer(buffer, value);
            this.generateSourceFile(file, buffer);
            
            for(MappingUnit unit: this.mirrorList) {
                this.createSourceFile(unit);
            }
        }
    }
    
    public void createSourceFile(MappingUnit unit) {
        
        final TypeDeclaration decl = (TypeDeclaration) unit.decl;

        final String[] sourceName = new String[1];
        final String[] author = new String[1];
        final String[] account = new String[1];
        final String[] pack = new String[1];
        final String[] newProjectName = new String[1];
        final AtomicInteger sourceType = new AtomicInteger(-1);
        
        File sourceFile = decl.getPosition().file();
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(sourceFile.getAbsolutePath()));
        IProject project = file.getProject();
        final String projectName = project.getName();
        
        Display dp = Display.getCurrent();
        if ( null == dp )
        {
            dp = Display.getDefault();
        }
        
        dp.syncExec(new Runnable() {
            
            @Override
            public void run() {
                Display newDp = Display.getCurrent();
                if(newDp == null) {
                    newDp = Display.getDefault();
                }
                
                CreateSourceDialog dialog = new CreateSourceDialog(newDp.getActiveShell(), projectName, decl.getPackage().getQualifiedName(), DEFAULT_AUTHOR, DEFAULT_ACCOUNT);
                dialog.setBlockOnOpen(true);
                dialog.open();
                
                if(dialog.isClosedByOk()) {
                    sourceName[0] = dialog.name;
                    author[0] = dialog.author;
                    account[0] = dialog.account;
                    pack[0] = dialog.pack;
                    newProjectName[0] = dialog.projectName;
                    sourceType.set(dialog.type);
                }
            }
        });
        
        int type = sourceType.get();
        
        if(type == -1) {
            return;
        }
        
        StringBuffer newBuffer = new StringBuffer();
        
        newBuffer.append(Boilerplate.BOILER1);
        newBuffer.append(BoilerplateAnnotationProcessor.getPluginName(pack[0]));
        newBuffer.append(Boilerplate.BOILER2);
        newBuffer.append(author[0] + " <" + account[0] + ">");
        newBuffer.append(Boilerplate.BOILER3);
        
        newBuffer.append(CreateSource.CREATE_SOURCE_1_PACKAGE);
        newBuffer.append(pack[0]);
        newBuffer.append(CreateSource.CREATE_SOURCE_2_TYPE);
        
        if(type == SourceType.ANNOTATION) {
            newBuffer.append("@interface");
        }
        else if(type == SourceType.ENUM) {
            newBuffer.append("enum");
        }
        else if(type == SourceType.INTERFACE) {
            newBuffer.append("interface");
        }
        else {
            newBuffer.append("class");
        }
        
        newBuffer.append(CreateSource.CREATE_SOURCE_3_NAME);
        newBuffer.append(sourceName[0]);
        newBuffer.append(CreateSource.CREATE_SOURCE_4);
        
        pack[0] = pack[0].replace('.', '/');
        
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(newProjectName[0]);
        String folderPath =  "src/" + pack[0];
        
        if(!newProject.exists()) {
            Logger.error("Project " + newProject.getName() + " does not exist");
            return;
        }
        IFolder folder = newProject.getFolder(folderPath);
        
        if(!folder.exists()) {
            try {
                folder.create(true, true, new NullProgressMonitor());
            } catch (CoreException e) {
                Logger.error("Exception occurred while creating folder: " + folder.getFullPath(), e);
            }
        }
        
        IFile newFile = newProject.getFile(folderPath + "/" + sourceName[0] + ".java");
        
        try {
            newFile.create(new ByteArrayInputStream(newBuffer.toString().getBytes()), IResource.FORCE, new NullProgressMonitor());
        } catch (CoreException e) {
            Logger.error("Exception occurred while writing file: " + newFile.getFullPath(), e);
            return;
        }
        
        try {
            newFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
        } catch (CoreException e) {
            Logger.error("Exception occurred while refreshing file: "+ newFile.getFullPath(), e);
        }
    
    }
    
    @Override
    public boolean appendBuffer(StringBuffer buffer, File sourceFile, List<MappingUnit> mappingList) {
        FileInputStream fIn = null;
        DataInputStream in = null;
        
        try {
            fIn = new FileInputStream(sourceFile);
            in = new DataInputStream(fIn);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            
            boolean importFound = false;
            String strLine = null;
            int i = 0;  
            MappingUnit targetUnit = mappingList.get(0);
            int targetLine = targetUnit.mirror.getPosition().line();
            mirrorList.add(targetUnit);
            mappingList.remove(0);
            
            while((strLine = br.readLine()) != null) {
                i++;
                if(!importFound) {
                    if(this.importName.equals(strLine.trim())) {
                        importFound = true;
                        continue;
                    }
                }
                if(i != targetLine) {
                    buffer.append(strLine + "\n");
                }
                else {
                    if(!strLine.contains(this.annotationName)) {
                        try {
                            fIn.close();
                        } catch (IOException e) {
                            Logger.error("Exception occured while closing FileInputStream of " + sourceFile.getAbsolutePath());
                        }
                        try {
                            in.close();
                        } catch (IOException e) {
                            Logger.error("Exception occured while closing DataInputStream of " + sourceFile.getAbsolutePath());
                        }
                        Logger.error("File : " + sourceFile.getAbsolutePath() + " does not sync with eclipse editor");
                        return false;
                    }
                    this.appendBufferInAnnotation(buffer, targetUnit);
                    if(mappingList.size() > 0) {
                        targetUnit = mappingList.get(0);
                        targetLine = targetUnit.mirror.getPosition().line();
                        this.mirrorList.add(targetUnit);
                        mappingList.remove(0);
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("Exception occured while reading file: " + sourceFile.getAbsolutePath(), e);
        } finally {
            if(fIn != null) {
                try {
                    fIn.close();
                } catch (IOException e) {
                    Logger.error("Exception occured while closing FileInputStream of " + sourceFile.getAbsolutePath());
                }
            }
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Logger.error("Exception occured while closing DataInputStream of " + sourceFile.getAbsolutePath());
                }
            }
        }
        return true;
    }
    
    @Override
    public void preAppendBuffer(StringBuffer buffer, List<MappingUnit> values) {
    }

    @Override
    public void postAppendBuffer(StringBuffer buffer, List<MappingUnit> values) {
    }

    @Override
    public void appendBufferInAnnotation(StringBuffer buffer, MappingUnit unit) {
    }

    @Override
    public boolean isOverwrite() {
        return true;
    }

    @Override
    public String getTargetAnnotationQualifiedName() {
        return CREATE_SOURCE_QUALIFIED_NAME;
    }

}
