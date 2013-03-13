package org.tizen.common.annotation.apt.factory;

import java.util.Set;

import org.tizen.common.annotation.apt.processor.NoteAnnotationProcessor;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

public class NoteAnnotationProcessorFactory extends AbstractAnnotationFactory{
    
    @Override
    public AnnotationProcessor getProcessorFor(
            Set<AnnotationTypeDeclaration> declarations,
            AnnotationProcessorEnvironment env) {
        AnnotationProcessor result;
        
        if(declarations.isEmpty()) {
            result = AnnotationProcessors.NO_OP;
        } else {
            result = new NoteAnnotationProcessor(env);
        }
        
        return result;
    }

    @Override
    public String getAnnotationQualifiedName() {
        return NoteAnnotationProcessor.NOTE_QUALIFIED_NAME;
    }

}
