package com.aork.user.repository;

import com.aork.base.resource.ListResource;
import com.aork.common.network.observer.ListResourceObserver;
import com.aork.common.network.service.CollectionService;
import com.aork.user.vm.UserCollectionsViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class UserCollectionsViewRepository {

    private CollectionService service;

    @Inject
    public UserCollectionsViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getUserCollections(@NonNull UserCollectionsViewModel viewModel,
                                   String username, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestUserCollections(
                username,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
