package org.tizen.common.annotation.apt.processor;

import java.util.Collection;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;

public abstract class MessageAbstractAnnotationProcessor extends AbstractAnnotationProcessor {

    protected Messager messager;
    public abstract void printMessage(MappingUnit unit);
    
    public MessageAbstractAnnotationProcessor(AnnotationProcessorEnvironment env, String[] storageNames) { 
        super(env, storageNames);
        
        this.messager = env.getMessager();
    }
    
    @Override
    public void process() {
        Collection<Declaration> declarations = this.environment.getDeclarationsAnnotatedWith(this.targetAnnotationDeclaration);
        
        for(Declaration decl: declarations) {
            for(AnnotationMirror mirror: decl.getAnnotationMirrors()) {
                
                AnnotationTypeDeclaration annotationDecl = mirror.getAnnotationType().getDeclaration();
               
                if(annotationDecl.equals(this.targetAnnotationDeclaration)) {
                    printMessage(new MappingUnit(decl, mirror));
                }
            }
        }
    }
}
