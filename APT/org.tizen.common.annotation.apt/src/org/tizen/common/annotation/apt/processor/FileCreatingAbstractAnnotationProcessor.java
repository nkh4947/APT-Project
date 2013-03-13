package org.tizen.common.annotation.apt.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.tizen.common.annotation.apt.util.Logger;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;

public abstract class FileCreatingAbstractAnnotationProcessor extends AbstractAnnotationProcessor{
    
    protected String importName;
    protected String annotationName;
    protected boolean overwrite;
    protected Map<File, List<MappingUnit>> mapper;
    protected List<IPath> blockFiles;
    
    protected static Comparator<MappingUnit> comparator = new Comparator<MappingUnit>() {
        @Override
        public int compare(MappingUnit o1, MappingUnit o2) {
            int line1 = o1.mirror.getPosition().line();
            int line2 = o2.mirror.getPosition().line();
            
            if(line1 < line2) {
                return -1;
            }
            if(line1 == line2) {
                return 0;
            }
            return 1;
        }
    };
    
    
    public abstract void preAppendBuffer(StringBuffer buffer, List<MappingUnit> values);
    public abstract void postAppendBuffer(StringBuffer buffer, List<MappingUnit> values);
    public abstract void appendBufferInAnnotation(StringBuffer buffer, MappingUnit unit);
    public abstract boolean isOverwrite();
    
    public FileCreatingAbstractAnnotationProcessor(AnnotationProcessorEnvironment env, String[] storageNames) {
        super(env, storageNames);
        
        int lastIndex = this.annotationQualifiedName.lastIndexOf(".");
        this.annotationName = "@" + this.annotationQualifiedName.substring(lastIndex + 1);
        this.importName = "import " + this.annotationQualifiedName + ";";
        this.mapper = new HashMap<File, List<MappingUnit>>();
        this.blockFiles = new ArrayList<IPath>();
        this.overwrite = this.isOverwrite();
    }
    
    @Override
    public void process() {
        makeAnnotationMap();
        createBlockFiles();
        
        for(Map.Entry<File, List<MappingUnit>> entry: this.mapper.entrySet()) {
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
        }
    }
    
    public void generateSourceFile(IFile file, StringBuffer buffer) {
        
        try {
            if(overwrite) {
                file.setContents(new ByteArrayInputStream(buffer.toString().getBytes()), IResource.FORCE, new NullProgressMonitor());
            }
            else {
                file.create(new ByteArrayInputStream(buffer.toString().getBytes()), IResource.FORCE, new NullProgressMonitor());
            }
            file.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
        } catch (CoreException e) {
            Logger.error("Exception occurred while writing file: " + file.getFullPath());
        }
    }
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
    
    public void makeAnnotationMap() {
        Collection<Declaration> declarations = this.environment.getDeclarationsAnnotatedWith(this.targetAnnotationDeclaration);
        this.mapper.clear();
        
        for(Declaration decl: declarations) {
            visitDeclarationForMakingAnnotationMap(decl);
        }
    }
    
    public void visitDeclarationForMakingAnnotationMap(Declaration d) {
        for(AnnotationMirror mirror: d.getAnnotationMirrors()) {
            
            AnnotationTypeDeclaration annotationDecl = mirror.getAnnotationType().getDeclaration();
           
            if(annotationDecl.equals(this.targetAnnotationDeclaration)) {
                File key = mirror.getPosition().file();
                
                if(this.mapper.containsKey(key)) {
                    this.mapper.get(key).add(new MappingUnit(d, mirror));
                }
                else {
                    List<MappingUnit> value = new ArrayList<MappingUnit>();
                    value.add(new MappingUnit(d, mirror));
                    this.mapper.put(key, value);
                }
            }
        }
    }
    
    public void createBlockFiles() {
        this.blockFiles.clear();
        
        final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
        
        
        Display dp = Display.getCurrent();
        if ( null == dp )
        {
            dp = Display.getDefault();
        }
        
        dp.syncExec(new Runnable() {
            
            @Override
            public void run() {
                window[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            }
        });
        
        for(IEditorReference editorRef : window[0].getActivePage().getEditorReferences()) {
            if(!editorRef.isDirty()){
                continue;
            }
            IEditorInput editorInput = null;
            try {
                editorInput = editorRef.getEditorInput();
            } catch (PartInitException e) {
                e.printStackTrace();
            }
            
            if(editorInput instanceof IFileEditorInput) {
                this.blockFiles.add(((IFileEditorInput)editorInput).getFile().getProjectRelativePath());
            }
        }
    }
    
    public boolean checkFileValidation(IFile file) {
        IPath sourcePath = file.getProjectRelativePath();
        
        for(IPath blockPath: this.blockFiles) {
            if(blockPath.toString().equals(sourcePath.toString())) {
                return false;
            }
        }
        return true;
    }
}
