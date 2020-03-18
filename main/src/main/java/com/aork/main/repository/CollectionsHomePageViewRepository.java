package com.aork.main.repository;

import com.aork.base.resource.ListResource;
import com.aork.common.network.observer.ListResourceObserver;
import com.aork.common.network.service.CollectionService;
import com.aork.main.vm.CollectionsHomePageViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class CollectionsHomePageViewRepository {

    private CollectionService service;

    @Inject
    public CollectionsHomePageViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getFeaturedCollections(@NonNull CollectionsHomePageViewModel viewModel, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestFeaturedCollections(
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
