package com.aork.common.di.component;

import com.aork.common.di.module.NetworkModule;
import com.aork.common.network.ComponentCollection;

import dagger.Component;

@Component(modules = NetworkModule.class)
public interface NetworkComponent {

    void inject(ComponentCollection collection);
}
