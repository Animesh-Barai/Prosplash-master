package com.aork.collection.di.component;

import com.aork.collection.CollectionActivity;
import com.aork.collection.di.module.ViewModelModule;
import com.aork.collection.ui.UpdateCollectionDialog;
import com.aork.common.di.module.NetworkServiceModule;

import dagger.Component;

@Component(modules = {NetworkServiceModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    void inject(CollectionActivity activity);

    void inject(UpdateCollectionDialog dialog);
}
