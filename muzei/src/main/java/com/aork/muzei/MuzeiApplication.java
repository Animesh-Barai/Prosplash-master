package com.aork.muzei;

import android.content.Context;

import com.aork.common.base.application.MultiModulesApplication;
import com.aork.component.ComponentFactory;

public class MuzeiApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setMuzeiService(new MuzeiServiceIMP());
    }
}
