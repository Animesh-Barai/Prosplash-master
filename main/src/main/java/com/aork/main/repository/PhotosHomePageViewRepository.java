package com.aork.main.repository;

import com.aork.base.resource.ListResource;
import com.aork.common.network.observer.ListResourceObserver;
import com.aork.common.network.service.PhotoService;
import com.aork.main.vm.PhotosHomePageViewModel;

import javax.inject.Inject;

public class PhotosHomePageViewRepository {

    private PhotoService service;

    @Inject
    public PhotosHomePageViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getPhotos(PhotosHomePageViewModel viewModel, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestPhotos(
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void cancel() {
        service.cancel();
    }
}
