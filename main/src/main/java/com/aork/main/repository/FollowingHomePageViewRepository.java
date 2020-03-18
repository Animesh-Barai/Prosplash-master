package com.aork.main.repository;

import com.aork.base.resource.ListResource;
import com.aork.common.network.observer.ListResourceObserver;
import com.aork.common.network.service.FeedService;
import com.aork.main.vm.FollowingHomePageViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class FollowingHomePageViewRepository {

    private FeedService service;

    @Inject
    public FollowingHomePageViewRepository(FeedService service) {
        this.service = service;
    }

    public void getFollowingFeeds(@NonNull FollowingHomePageViewModel viewModel,
                                  boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestFollowingFeed(
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh));
    }

    public void cancel() {
        service.cancel();
    }
}
