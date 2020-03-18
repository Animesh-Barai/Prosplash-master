package com.aork.common.base.popup;

import android.view.View;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.aork.common.base.activity.MysplashActivity;

/**
 * Mysplash popup window.
 *
 * Basic PopupWindow class for Mysplash.
 *
 * */

public class MysplashPopupWindow {

    public static void show(MysplashActivity activity, View anchor, @MenuRes int resId,
                            @NonNull PopupMenu.OnMenuItemClickListener l) {
        PopupMenu popupMenu = new PopupMenu(activity, anchor);
        popupMenu.getMenuInflater().inflate(resId, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(l);

        // DisplayUtils.setOverflowMenuIconsVisible(popupMenu.getMenu());

        popupMenu.show();
    }
}
