package com.aork.common.ui.widget.insets;

import android.graphics.Rect;
import android.view.WindowInsets;

public class Utils {
    static Rect getWaterfullInsets(WindowInsets insets) {
        Rect waterfull = new Rect();/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout != null) {
                Insets i = cutout.getWaterfallInsets();
                waterfull = new Rect(i.left, i.top, i.right, i.bottom);
            }
        }*/
        return waterfull;
    }
}
