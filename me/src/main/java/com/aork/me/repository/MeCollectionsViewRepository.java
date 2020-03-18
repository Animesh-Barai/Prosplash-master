package com.aork.me.repository;

import android.text.TextUtils;

import com.aork.base.resource.ListResource;
import com.aork.common.network.observer.ListResourceObserver;
import com.aork.common.network.service.CollectionService;
import com.aork.common.utils.manager.AuthManager;
import com.aork.me.vm.MeCollectionsViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;

public class MeCollectionsViewRepository {

    private CollectionService service;

    @Inject
    public MeCollectionsViewRepository(CollectionService service) {
        this.service = service;
    }

    public void getUserCollections(@NonNull MeCollectionsViewModel viewModel,
                                   boolean refresh) {
        if (!TextUtils.isEmpty(AuthManager.getInstance().getUsername())) {
            if (refresh) {
                viewModel.writeListResource(ListResource::refreshing);
            } else {
                viewModel.writeListResource(ListResource::loading);
            }

            service.cancel();
            service.requestUserCollections(
                    AuthManager.getInstance().getUsername(),
                    viewModel.getListRequestPage(),
                    viewModel.getListPerPage(),
                    new ListResourceObserver<>(viewModel, refresh)
            );
        }
    }

    public void cancel() {
        service.cancel();
    }
}
