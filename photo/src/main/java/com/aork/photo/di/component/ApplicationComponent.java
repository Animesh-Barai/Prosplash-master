package com.aork.photo.di.component;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.photo.activity.PhotoActivity;
import com.aork.photo.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(PhotoActivity activity);
}
