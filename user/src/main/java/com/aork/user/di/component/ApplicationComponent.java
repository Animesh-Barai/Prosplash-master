package com.aork.user.di.component;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.user.UserActivity;
import com.aork.user.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(UserActivity activity);
}
