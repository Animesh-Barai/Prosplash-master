package com.aork.settings;

import android.content.Context;

import com.aork.common.base.application.MultiModulesApplication;
import com.aork.component.ComponentFactory;

public class SettingsApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setSettingsService(SettingsServiceIMP.getInstance(context));
    }
}
