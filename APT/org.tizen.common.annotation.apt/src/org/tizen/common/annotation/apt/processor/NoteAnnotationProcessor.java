package org.tizen.common.annotation.apt.processor;

import org.eclipse.core.runtime.IStatus;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.util.SourcePosition;

public class NoteAnnotationProcessor extends MessageAbstractAnnotationProcessor{
    
    public static final String NOTE_QUALIFIED_NAME = "org.tizen.common.annotation.Note";
    public static final String DEFAULT_ACCOUNT = "ho.namkoong@samsung.com";
    
    private static final String[] ANNOTATION_TAGS = {
            "author", 
            "assignee", 
            "type", 
            "message", 
            "redmineNumber"
    };
    
    public NoteAnnotationProcessor(AnnotationProcessorEnvironment env) {
        super(env, ANNOTATION_TAGS);
    }

    @Override
    public String getTargetAnnotationQualifiedName() {
        return NOTE_QUALIFIED_NAME;
    }

    @Override
    public void printMessage(MappingUnit unit) {
        AnnotationMirror mirror = unit.mirror;
        Object[] annotationValues = this.getAnotationValues(mirror);
        
        Object annotationAuthor = annotationValues[0];
        Object annotationAssignee = annotationValues[1];
        Object annotationType = annotationValues[2];
        Object annotationMessage = annotationValues[3];
        Object annotationRedmineN = annotationValues[4];

        if(DEFAULT_ACCOUNT.equals(annotationAssignee)) {
            
            StringBuffer buffer = new StringBuffer();
            boolean first = true;
            if(annotationAuthor != null) {
                buffer.append("Author: " + annotationAuthor);
                first = false;
            }
            
            if(annotationMessage != null) {
                if(!first) {
                    buffer.append("\n");
                }
                else {
                    first = false;
                }
                buffer.append("Message: " + annotationMessage);
            }
            
            if(annotationRedmineN != null) {
                if(!first) {
                    buffer.append("\n");
                }
                buffer.append("Redmine #: " + annotationRedmineN);
            }
            
            SourcePosition pos = unit.decl.getPosition();
            
            int type = 0;
            if(annotationType != null) {
                type = (Integer) annotationType;
                
                if(type == IStatus.ERROR) {
                    messager.printError(pos, buffer.toString());
                    return;
                }
                if(type == IStatus.INFO) {
                    messager.printNotice(pos, buffer.toString());
                    return;
                }
            }
            messager.printWarning(pos, buffer.toString());
        }
        

    }
}
