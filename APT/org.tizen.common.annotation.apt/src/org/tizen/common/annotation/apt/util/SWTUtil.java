package org.tizen.common.annotation.apt.util;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

public class SWTUtil {
    
    public static void setGridLayoutData(Control c, int heightHint, int widthHint, int horizontalSpan, int verticalSpan, int style) {
        GridData gd = new GridData(style);
        if(heightHint != -1) {
            gd.heightHint = heightHint;
        }
        if(widthHint != -1) {
            gd.widthHint = widthHint;
        }
        if(horizontalSpan != -1) {
            gd.horizontalSpan = horizontalSpan;
        }
        if(verticalSpan != -1) {
            gd.verticalSpan = verticalSpan;
        }
        c.setLayoutData(gd);
    }
}
