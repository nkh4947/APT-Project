package org.tizen.common.annotation.apt.processor;

import java.util.List;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.TypeDeclaration;
import org.tizen.common.annotation.apt.template.Boilerplate;

public class BoilerplateAnnotationProcessor extends FileCreatingAbstractAnnotationProcessor{
    
    private static final String[] ANNOTATION_TAGS = {
        "author",
        "account",
        "plugin"
    };
    
    private static final String DEFAULT_AUTHOR = "Ho Namkoong";
    private static final String DEFAULT_ACCOUNT = "ho.namkoong@samsung.com";
    
    public static final String BOILERPLATE_QUALIFIED_NAME = "org.tizen.common.annotation.Boilerplate";
    private static final String PLUGIN_COMMON = "org.tizen.common";
    private static final String PLUGIN_NATIVECOMMON = "org.tizen.nativecommon";
    private static final String PLUGIN_NATIVEAPPCOMMON = "org.tizen.nativeappcommon";
    public BoilerplateAnnotationProcessor(AnnotationProcessorEnvironment env) {
        super(env, ANNOTATION_TAGS);
    }

    @Override
    public void preAppendBuffer(StringBuffer buffer, List<MappingUnit> units) {
        MappingUnit unit = units.get(0);
        AnnotationMirror mirror = unit.mirror;
        Object[] annotationValues =  this.getAnotationValues(mirror);
        
        Object annotAuthor = annotationValues[0];
        Object annotAccount = annotationValues[1];
        Object annotPlugin = annotationValues[2];
        String author;
        String account;
        String plugin;
        
        if(annotAuthor != null) {
            author = (String) annotAuthor;
        } 
        else {
            author = DEFAULT_AUTHOR;
        }
        
        if(annotAccount != null) {
            account = (String) annotAccount;
        }
        else {
            account = DEFAULT_ACCOUNT;
        }
        
        if(annotPlugin != null) {
            plugin = (String) annotPlugin;
        } 
        else {
            TypeDeclaration decl = (TypeDeclaration) unit.decl;
            plugin = getPluginName(decl.getPackage().getQualifiedName());
        }
        
        buffer.append(Boilerplate.BOILER1);
        buffer.append(plugin);
        buffer.append(Boilerplate.BOILER2);
        buffer.append(author + " <" + account + ">");
        buffer.append(Boilerplate.BOILER3);
    }

    @Override
    public void postAppendBuffer(StringBuffer buffer, List<MappingUnit> units) {
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
        return BOILERPLATE_QUALIFIED_NAME;
    }
    
    static String getPluginName(String packageName) {
        if(packageName.contains(PLUGIN_COMMON)) {
            return "Common";
        }
        if(packageName.contains(PLUGIN_NATIVECOMMON)) {
            return "NativeCommon";
        }
        if(packageName.contains(PLUGIN_NATIVEAPPCOMMON)) {
            return "NativeAppCommon";
        }
        return "";
    }
}
