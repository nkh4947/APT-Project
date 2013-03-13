package org.tizen.common.annotation.apt.util;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.tizen.common.annotation.apt.Activator;

public class Logger {
    private static String loggerName = Logger.class.getName();

    public static void log(IStatus status) {
        Activator plugin = Activator.getDefault();
        if (plugin != null) {
            plugin.getLog().log(status);
        }
    }

    private static String getCallerName() {
        // Get the stack trace.
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        int ix = 0;
        // Now search for the first frame before the "Logger" class.
        while (ix < stack.length) {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (!cname.equals(loggerName)) {
                return cname;
            }
            ix++;
        }
        return "noname";
    }

    public static void log(Throwable e) {
        if (e instanceof CoreException) {
            log(new Status(IStatus.ERROR, getCallerName(), ((CoreException) e).getStatus().getSeverity(), e.getMessage(), e.getCause()));
        } else {
            log(new Status(IStatus.ERROR, getCallerName(), e.toString(), e));
        }
    }
    
    public static void debug( String message, Object... arguments )
    {
        
    }

    public static void info(String message, Object... arguments) {
        log(new Status(Status.INFO, getCallerName(), getPossiblyFormattedString(message, arguments)));
    }

    public static void error(Object message, Throwable t) {
        log(new Status(Status.ERROR, getCallerName(), message.toString(), t));
    }

    public static void error(String message, Object... arguments) {
        log(new Status(Status.ERROR, getCallerName(), getPossiblyFormattedString(message, arguments)));
    }

    public static void warning(String message, Object... arguments) {
        log(new Status(Status.WARNING, getCallerName(), getPossiblyFormattedString(message, arguments)));
    }

    private static String getPossiblyFormattedString(String message, Object... arguments) {
        return arguments.length > 0 ? MessageFormat.format(message, arguments)
                                                  : message;
    }

}