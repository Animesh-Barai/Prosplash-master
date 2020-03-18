package com.aork.search.di.component;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.search.SearchActivity;
import com.aork.search.di.module.ViewModelModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(SearchActivity activity);
}
