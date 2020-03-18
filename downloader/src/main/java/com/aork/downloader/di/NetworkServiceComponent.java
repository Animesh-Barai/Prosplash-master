package com.aork.downloader.di;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.downloader.DownloaderServiceIMP;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(DownloaderServiceIMP helper);
}
