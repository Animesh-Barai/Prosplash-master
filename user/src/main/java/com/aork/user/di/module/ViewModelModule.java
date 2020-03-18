package com.aork.user.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.aork.common.base.vm.PagerManageViewModel;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.di.annotation.ViewModelKey;
import com.aork.user.vm.UserActivityModel;
import com.aork.user.vm.UserCollectionsViewModel;
import com.aork.user.vm.UserLikesViewModel;
import com.aork.user.vm.UserPhotosViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(UserActivityModel.class)
    public abstract ViewModel getUserActivityModel(UserActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(UserCollectionsViewModel.class)
    public abstract ViewModel getUserCollectionsViewModel(UserCollectionsViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(UserLikesViewModel.class)
    public abstract ViewModel getUserLikesViewModel(UserLikesViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(UserPhotosViewModel.class)
    public abstract ViewModel getUserPhotosViewModel(UserPhotosViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
