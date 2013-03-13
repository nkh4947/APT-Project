package org.tizen.common.annotation.apt.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;

public class ProcessingUtil {

    public static void visitDeclaration(Declaration d, AnnotationTypeDeclaration annotationDeclaration, Map<File, List<AnnotationMirror>> mapper) {
        for(AnnotationMirror mirror: d.getAnnotationMirrors()) {
            
            AnnotationTypeDeclaration annotationDecl = mirror.getAnnotationType().getDeclaration();
            if(annotationDecl.equals(annotationDeclaration)) {
                File key = annotationDecl.getPosition().file();
                
                if(mapper.containsKey(key)) {
                    mapper.get(key).add(mirror);
                }
                else {
                    List<AnnotationMirror> value = new ArrayList<AnnotationMirror>();
                    mapper.put(key, value);
                }
            }
        }
    }
    
}
