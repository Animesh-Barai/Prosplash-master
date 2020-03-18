package com.aork.about;

import android.content.Context;

import com.aork.common.base.application.MultiModulesApplication;
import com.aork.component.ComponentFactory;

public class AboutApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setAboutModule(new AboutModuleIMP());
    }
}
