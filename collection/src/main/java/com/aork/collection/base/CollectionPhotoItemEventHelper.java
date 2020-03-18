package com.aork.collection.base;

import com.aork.collection.CollectionActivity;
import com.aork.base.unsplash.Photo;
import com.aork.collection.vm.CollectionPhotosViewModel;
import com.aork.common.bus.MessageBus;
import com.aork.common.bus.event.CollectionEvent;
import com.aork.common.bus.event.PhotoEvent;
import com.aork.common.ui.adapter.photo.PhotoItemEventHelper;
import com.aork.common.ui.dialog.DeleteCollectionPhotoDialog;

public class CollectionPhotoItemEventHelper extends PhotoItemEventHelper {

    private CollectionActivity activity;

    public CollectionPhotoItemEventHelper(CollectionActivity activity, CollectionPhotosViewModel viewModel,
                                          DownloadExecutor executor) {
        super(activity, viewModel, executor);
        this.activity = activity;
    }

    @Override
    public void onDeleteButtonClicked(Photo photo, int adapterPosition) {
        DeleteCollectionPhotoDialog dialog = new DeleteCollectionPhotoDialog();
        dialog.setDeleteInfo(activity.getCollection(), photo);
        dialog.setOnDeleteCollectionListener(result -> {
            MessageBus.getInstance().post(result.user);

            MessageBus.getInstance().post(new CollectionEvent(
                    result.collection, CollectionEvent.Event.UPDATE));

            MessageBus.getInstance().post(PhotoEvent.collectOrRemove(result.photo, result.collection));
        });
        dialog.show(activity.getSupportFragmentManager(), null);
    }
}
