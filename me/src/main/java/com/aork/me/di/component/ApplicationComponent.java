package com.aork.me.di.component;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.me.activity.LoginActivity;
import com.aork.me.activity.MeActivity;
import com.aork.me.activity.MyFollowActivity;
import com.aork.me.activity.UpdateMeActivity;
import com.aork.me.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(MeActivity activity);
    void inject(MyFollowActivity activity);
    void inject(LoginActivity activity);
    void inject(UpdateMeActivity activity);
}
