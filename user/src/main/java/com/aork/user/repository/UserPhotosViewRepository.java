package com.aork.user.repository;

import com.aork.base.resource.ListResource;
import com.aork.common.network.observer.ListResourceObserver;
import com.aork.common.network.service.PhotoService;
import com.aork.user.vm.UserPhotosViewModel;

import javax.inject.Inject;

public class UserPhotosViewRepository {

    private PhotoService service;

    @Inject
    public UserPhotosViewRepository(PhotoService service) {
        this.service = service;
    }

    public void getUserPhotos(UserPhotosViewModel viewModel, String username, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestUserPhotos(
                username,
                viewModel.getListRequestPage(),
                viewModel.getListPerPage(),
                new ListResourceObserver<>(viewModel, refresh)
        );
    }

    public void getUserLikes(UserPhotosViewModel viewModel, String username, boolean refresh) {
        if (refresh) {
            viewModel.writeListResource(ListResource::refreshing);
        } else {
            viewModel.writeListResource(ListResource::loading);
        }

        service.cancel();
        service.requestUserLikes(
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
