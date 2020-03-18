package com.aork.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.aork.common.base.application.MultiModulesApplication;
import com.aork.component.ComponentFactory;
import com.aork.component.module.MainModule;

public class MainApplication extends MultiModulesApplication {

    private class MainModuleIMP implements MainModule {

        @Override
        public void startMainActivity(Activity activity) {
            ARouter.getInstance()
                    .build(MainActivity.MAIN_ACTIVITY)
                    .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .navigation(activity);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setMainModule(new MainModuleIMP());
    }
}
