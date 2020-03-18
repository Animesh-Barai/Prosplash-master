package com.aork.main.di.component;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.main.MainActivity;
import com.aork.main.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);
}
