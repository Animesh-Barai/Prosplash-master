package com.aork.photo.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.aork.common.base.vm.PagerManageViewModel;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.di.annotation.ViewModelKey;
import com.aork.photo.vm.PhotoActivityModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(PhotoActivityModel.class)
    public abstract ViewModel getPhotoActivityModel(PhotoActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
