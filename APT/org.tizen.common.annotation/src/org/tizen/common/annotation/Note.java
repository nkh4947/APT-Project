package org.tizen.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.eclipse.core.runtime.IStatus;

@Documented
@Retention(value = RetentionPolicy.SOURCE)
public @interface Note {
    String author() default "";
    String assignee() default "";
    int type() default IStatus.INFO;
    String message() default "";
    int redmineNumber() default -1;
}
