package com.aork.collection.repository;

import javax.inject.Inject;

import androidx.annotation.NonNull;

import com.aork.base.resource.ListResource;
import com.aork.collection.vm.CollectionPhotosViewModel;
import com.aork.common.network.observer.ListResourceObserver;
import com.aork.common.network.service.PhotoService;

public class CollectionPhotosViewRepository {

    private PhotoService service;

    @Inject
    public CollectionPhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getCollectionPhotos(@NonNull CollectionPhotosViewModel viewModel,
                                    int collectionId, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestCollectionPhotos(
                collectionId,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void getCuratedCollectionPhotos(@NonNull CollectionPhotosViewModel viewModel,
                                           int collectionId, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestCuratedCollectionPhotos(
                collectionId,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
