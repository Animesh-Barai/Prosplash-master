package com.aork.muzei.di;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.muzei.provider.MysplashMuzeiWorker;
import com.aork.muzei.service.MysplashMuzeiArtSource;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(MysplashMuzeiWorker helper);

    void inject(MysplashMuzeiArtSource source);
}
