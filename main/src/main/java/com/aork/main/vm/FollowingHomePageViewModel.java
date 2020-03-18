package com.aork.main.vm;

import com.aork.base.resource.ListResource;
import com.aork.common.base.vm.pager.PhotosPagerViewModel;
import com.aork.base.unsplash.Photo;
import com.aork.main.repository.FollowingHomePageViewRepository;

import javax.inject.Inject;

import androidx.annotation.NonNull;

/**
 * Following feed view model.
 * */
public class FollowingHomePageViewModel extends PhotosPagerViewModel {

    private FollowingHomePageViewRepository repository;

    @Inject
    public FollowingHomePageViewModel(FollowingHomePageViewRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public boolean init(@NonNull ListResource<Photo> resource) {
        if (super.init(resource)) {
            refresh();
            return true;
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancel();
    }

    @Override
    public void refresh() {
        repository.getFollowingFeeds(this, true);
    }

    @Override
    public void load() {
        repository.getFollowingFeeds(this, false);
    }
}
