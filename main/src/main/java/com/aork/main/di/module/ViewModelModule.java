package com.aork.main.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.aork.common.base.vm.PagerManageViewModel;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.di.annotation.ViewModelKey;
import com.aork.main.vm.CollectionsHomePageViewModel;
import com.aork.main.vm.FollowingHomePageViewModel;
import com.aork.main.vm.MainActivityModel;
import com.aork.main.vm.PhotosHomePageViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ParamsViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(CollectionsHomePageViewModel.class)
    public abstract ViewModel getAllCollectionsViewModel(CollectionsHomePageViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(FollowingHomePageViewModel.class)
    public abstract ViewModel getFollowingFeedViewModel(FollowingHomePageViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PhotosHomePageViewModel.class)
    public abstract ViewModel getFeaturedHomePhotosViewModel(PhotosHomePageViewModel model);

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityModel.class)
    public abstract ViewModel getMainActivityModel(MainActivityModel model);

    @Binds
    @IntoMap
    @ViewModelKey(PagerManageViewModel.class)
    public abstract ViewModel getPagerManageViewModel(PagerManageViewModel model);
}
