package com.aork.db;

import android.content.Context;

import com.aork.common.base.application.MultiModulesApplication;
import com.aork.component.ComponentFactory;

public class DatabaseApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setDatabaseService(DatabaseServiceIMP.getInstance(context));
    }
}
