package com.aork.photo.repository;

import com.aork.base.resource.ListResource;
import com.aork.base.resource.Resource;
import com.aork.base.unsplash.Photo;
import com.aork.common.network.observer.BaseObserver;
import com.aork.common.network.service.PhotoService;
import com.aork.common.bus.event.PhotoEvent;
import com.aork.common.bus.MessageBus;
import com.aork.photo.vm.PhotoActivityModel;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivityRepository {

    private PhotoService photoService;

    @Inject
    public PhotoActivityRepository(PhotoService photoService) {
        this.photoService = photoService;
    }

    public void getAPhoto(PhotoActivityModel viewModel, String id) {
        MutableLiveData<Resource<Photo>> current = viewModel.getResource();
        assert current.getValue() != null;
        viewModel.setPhoto(Resource.loading(current.getValue().data), false);

        photoService.cancel();
        photoService.requestAPhoto(id, new BaseObserver<Photo>() {
            @Override
            public void onSucceed(Photo photo) {
                assert viewModel.getListResource().getValue() != null;
                if (viewModel.getListResource().getValue().dataList.size() == 0) {
                    List<Photo> list = new ArrayList<>();
                    list.add(photo);
                    viewModel.getListResource().setValue(
                            ListResource.refreshSuccess(
                                    viewModel.getListResource().getValue(),
                                    list
                            )
                    );
                }

                MessageBus.getInstance().post(PhotoEvent.complete(photo));
                // handel update result by message bus.
                /*
                if (current.getValue() == null
                        || current.getValue().data == null
                        || current.getValue().data.id.equals(id)) {
                    viewModel.setPhoto(Resource.success(photo), false);
                }*/
            }

            @Override
            public void onFailed() {
                if (current.getValue() == null || current.getValue().data == null) {
                    viewModel.setPhoto(Resource.error(null), false);
                } else if (current.getValue().data.id.equals(id)) {
                    viewModel.setPhoto(Resource.error(current.getValue().data), false);
                }
            }
        });
    }

    public void cancel() {
        photoService.cancel();
    }
}
