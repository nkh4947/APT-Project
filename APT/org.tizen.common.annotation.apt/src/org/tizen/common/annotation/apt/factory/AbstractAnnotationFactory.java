package org.tizen.common.annotation.apt.factory;

import java.util.Collection;
import java.util.Collections;

import com.sun.mirror.apt.AnnotationProcessorFactory;

public abstract class AbstractAnnotationFactory implements AnnotationProcessorFactory{
    
    public abstract String getAnnotationQualifiedName();
    
    @Override
    public Collection<String> supportedOptions() {
        return Collections.emptyList();
    }
    
    @Override
    public Collection<String> supportedAnnotationTypes() {
        return Collections.singletonList(getAnnotationQualifiedName());
    }
}
