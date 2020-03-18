package com.aork.collection.repository;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aork.base.resource.Resource;
import com.aork.base.unsplash.Collection;
import com.aork.common.network.observer.ResourceObserver;
import com.aork.common.network.service.CollectionService;

public class CollectionActivityRepository {

    private CollectionService service;

    @Inject
    public CollectionActivityRepository(CollectionService service) {
        this.service = service;
    }

    public void getACollection(@NonNull MutableLiveData<Resource<Collection>> current, String id) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        service.cancel();
        service.requestACollections(id, new ResourceObserver<>(current));
    }

    public void getACuratedCollection(@NonNull MutableLiveData<Resource<Collection>> current, String id) {
        assert current.getValue() != null;
        current.setValue(Resource.loading(current.getValue().data));

        service.cancel();
        service.requestACuratedCollections(id, new ResourceObserver<>(current));
    }

    public void cancel() {
        service.cancel();
    }
}
