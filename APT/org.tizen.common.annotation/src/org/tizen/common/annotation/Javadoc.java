package org.tizen.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value = RetentionPolicy.SOURCE)
@Target(value = {ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Javadoc {
    String author() default "";
    String account() default "";
}
