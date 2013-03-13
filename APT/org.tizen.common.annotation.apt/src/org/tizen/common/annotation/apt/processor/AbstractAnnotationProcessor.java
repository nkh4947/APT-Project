package org.tizen.common.annotation.apt.processor;

import java.util.Map;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.Declaration;

public abstract class AbstractAnnotationProcessor implements AnnotationProcessor{
    
    protected AnnotationTypeDeclaration targetAnnotationDeclaration;
    protected AnnotationProcessorEnvironment environment;
    protected String annotationQualifiedName;
    protected String[] annotationTags;
    
    public abstract String getTargetAnnotationQualifiedName();
    
    public AbstractAnnotationProcessor(AnnotationProcessorEnvironment env, String[] annotationTags) {
        this.environment = env;
        this.annotationTags = annotationTags;
        
        this.annotationQualifiedName = this.getTargetAnnotationQualifiedName();
        this.targetAnnotationDeclaration = (AnnotationTypeDeclaration)env.getTypeDeclaration(this.annotationQualifiedName);
    }
    
    public Object[] getAnotationValues(AnnotationMirror mirror) {
        
        Map<AnnotationTypeElementDeclaration, AnnotationValue> elementValues = mirror.getElementValues();
        
        Object[] result = new Object[this.annotationTags.length];
        for(Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry: elementValues.entrySet()) {
            
            AnnotationTypeElementDeclaration key = entry.getKey();
            for(int i=0; i<this.annotationTags.length; i++) {
                
                //Annotation value cannot be null. So by checking value, we can know whether it is filled or not.
                if(result[i] != null) {
                    continue;
                }
                
                String annotationTag = this.annotationTags[i];
                if(key.getSimpleName().equals(annotationTag)) {
                    result[i] = entry.getValue().getValue();
                }
            }
        }
        
        return result;
    }
    
    class MappingUnit {
        
        public Declaration decl;
        public AnnotationMirror mirror;
        
        public MappingUnit(Declaration decl, AnnotationMirror mirror) {
            this.decl = decl;
            this.mirror = mirror;
        }
        
    }
}
