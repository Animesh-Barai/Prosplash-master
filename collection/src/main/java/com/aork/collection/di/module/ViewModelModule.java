package com.aork.collection.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.aork.collection.vm.CollectionActivityModel;
import com.aork.collection.vm.CollectionPhotosViewModel;
import com.aork.common.base.vm.PagerManageViewModel;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.di.annotation.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(CollectionActivityModel.class)
    public abstract ViewModel getCollectionActivityModel(CollectionActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(CollectionPhotosViewModel.class)
    public abstract ViewModel getCollectionPhotosViewModel(CollectionPhotosViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
