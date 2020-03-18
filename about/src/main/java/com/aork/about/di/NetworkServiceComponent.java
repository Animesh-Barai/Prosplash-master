package com.aork.about.di;

import com.aork.about.ui.TotalDialog;
import com.aork.common.di.module.NetworkServiceModule;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(TotalDialog dialog);
}
