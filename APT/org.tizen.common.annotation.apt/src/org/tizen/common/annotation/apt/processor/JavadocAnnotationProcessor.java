package org.tizen.common.annotation.apt.processor;

import java.util.List;

import org.tizen.common.annotation.apt.template.Javadoc;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.ReferenceType;
import com.sun.mirror.type.VoidType;

public class JavadocAnnotationProcessor extends FileCreatingAbstractAnnotationProcessor {
    
    private static final String[] ANNOTATION_TYPE_DEFS = {
        "author", "account"
    };

    public static final String JAVADOC_QUALIFIED_NAME = "org.tizen.common.annotation.Javadoc";
    
    private String author = "Ho Namkoong";
    private String account = "ho.namkoong@samsung.com";
    
    public JavadocAnnotationProcessor(AnnotationProcessorEnvironment env) {
        super(env, ANNOTATION_TYPE_DEFS);
    }

    @Override
    public String getTargetAnnotationQualifiedName() {
        return JAVADOC_QUALIFIED_NAME;
    }

    @Override
    public void preAppendBuffer(StringBuffer buffer, List<MappingUnit> unit) {
    }

    @Override
    public void postAppendBuffer(StringBuffer buffer, List<MappingUnit> unit) {
    }

    @Override
    public void appendBufferInAnnotation(StringBuffer buffer, MappingUnit unit) {
        Declaration decl = unit.decl;
        
        Object[] annotationValues = this.getAnotationValues(unit.mirror);
        
        Object authorStorage = annotationValues[0];
        Object accountStorage = annotationValues[1];
        
        String author;
        String account;
        
        if(authorStorage != null) {
            author = (String) authorStorage;
        } 
        else {
            author = this.author;
        }
        
        if(accountStorage != null) {
            account = (String) accountStorage;
        }
        else {
            account = this.account;
        }
        
        if(decl instanceof TypeDeclaration) {
            createTypeJavadoc(buffer, (TypeDeclaration)decl, author, account);
            return;
        }
        if(decl instanceof FieldDeclaration) {
            createFiledJavadoc(buffer);
            return;
        }
        if(decl instanceof MethodDeclaration) {
            createMethodJavadoc(buffer, (MethodDeclaration)decl, author, account);
            return;
        }
        if(decl instanceof ConstructorDeclaration) {
            createConstructorJavadoc(buffer, (ConstructorDeclaration)decl, author, account);
        }
        
    }

    private void createConstructorJavadoc(StringBuffer buffer, ConstructorDeclaration decl, String author, String account) {
        buffer.append(Javadoc.CONSTRUCTOR1_TYPE_NAME);
        buffer.append(decl.getSimpleName());
        buffer.append(Javadoc.CONSTRUCTOR2);
        
        boolean found = false;
        for(ParameterDeclaration param: decl.getParameters()) {
            buffer.append(Javadoc.CONSTRUCTOR2_1_PARAM);
            buffer.append(param.getSimpleName());
            found = true;
        }
        
        if(found) {
            buffer.append(Javadoc.CONSTRUCTOR0_SPACE);
            found = false;
        }
        
        for(ReferenceType throwType: decl.getThrownTypes()) {
            if(throwType instanceof DeclaredType) {
                found = true;
                buffer.append(Javadoc.CONSTRUCTOR2_2_THROWS);
                buffer.append(((DeclaredType)throwType).getDeclaration().getSimpleName());
            }
        }
        
        if(found) {
            buffer.append(Javadoc.CONSTRUCTOR0_SPACE);
            found = false;
        }
        
        buffer.append(Javadoc.CONSTRUCTOR3_AUTHOR);
        buffer.append(author);
        buffer.append(Javadoc.CONSTRUCTOR4_ACCOUNT);
        buffer.append(account);
        buffer.append(Javadoc.CONSTRUCTOR5);
    }

    private void createMethodJavadoc(StringBuffer buffer, MethodDeclaration decl, String author, String account) {
        buffer.append(Javadoc.METHOD1);
        
        boolean found = false;
        for(ParameterDeclaration param: decl.getParameters()) {
            buffer.append(Javadoc.METHOD1_1_PARAM);
            buffer.append(param.getSimpleName());
            found = true;
        }
        
        if(found) {
            buffer.append(Javadoc.METHOD0_SPACE);
            found = false;
        }
        
        if(!(decl.getReturnType() instanceof VoidType)) {
            buffer.append(Javadoc.METHOD1_2_RETURN);
            buffer.append(Javadoc.METHOD0_SPACE);
        }
        
        for(ReferenceType throwType: decl.getThrownTypes()) {
            
            if(throwType instanceof DeclaredType) {
                buffer.append(Javadoc.METHOD1_3_THROWS);
                buffer.append(((DeclaredType)throwType).getDeclaration().getSimpleName());
            }
        }
        
        buffer.append(Javadoc.METHOD2_AUTHOR);
        buffer.append(author);
        buffer.append(Javadoc.METHOD3_ACCOUNT);
        buffer.append(account);
        buffer.append(Javadoc.METHOD4);
        
    }

    private void createFiledJavadoc(StringBuffer buffer) {
        buffer.append(Javadoc.FIELD);
    }

    private void createTypeJavadoc(StringBuffer buffer, TypeDeclaration decl, String author, String account) {
        buffer.append(Javadoc.TYPE1_TYPE_NAME);
        buffer.append(decl.getSimpleName());
        buffer.append(Javadoc.TYPE2_AUTHOR);
        buffer.append(author);
        buffer.append(Javadoc.TYPE3_ACCOUNT);
        buffer.append(account);
        buffer.append(Javadoc.TYPE4);
    }

    @Override
    public boolean isOverwrite() {
        return true;
    }
}
