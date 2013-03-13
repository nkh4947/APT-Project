package org.tizen.common.annotation.apt.factory;

import java.util.Set;

import org.tizen.common.annotation.apt.processor.JavadocAnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class JavadocAnnotationFactory extends AbstractAnnotationFactory{

    
    
    @Override
    public AnnotationProcessor getProcessorFor(
            Set<AnnotationTypeDeclaration> declarations,
            AnnotationProcessorEnvironment env) {
        AnnotationProcessor result;
        
        if(declarations.isEmpty()) {
            result = AnnotationProcessors.NO_OP;
        } else {
            result = new JavadocAnnotationProcessor(env);
        }
        
        return result;
    }

    @Override
    public String getAnnotationQualifiedName() {
        return JavadocAnnotationProcessor.JAVADOC_QUALIFIED_NAME;
    }

}
