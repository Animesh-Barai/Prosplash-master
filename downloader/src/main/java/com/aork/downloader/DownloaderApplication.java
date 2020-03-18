package com.aork.downloader;

import android.content.Context;

import com.aork.common.base.application.MultiModulesApplication;
import com.aork.component.ComponentFactory;

public class DownloaderApplication extends MultiModulesApplication {

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setDownloaderService(
                DownloaderServiceIMP.getInstance(
                        context,
                        ComponentFactory.getSettingsService().getDownloader()
                )
        );
    }
}
