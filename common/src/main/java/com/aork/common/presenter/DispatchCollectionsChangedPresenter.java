package com.aork.common.presenter;

import com.aork.base.unsplash.Collection;
import com.aork.base.unsplash.Photo;
import com.aork.base.unsplash.User;
import com.aork.common.bus.MessageBus;
import com.aork.common.bus.event.CollectionEvent;
import com.aork.common.bus.event.PhotoEvent;
import com.aork.common.ui.dialog.SelectCollectionDialog;
import com.aork.common.utils.manager.AuthManager;

public class DispatchCollectionsChangedPresenter
        implements SelectCollectionDialog.OnCollectionsChangedListener {

    @Override
    public void onAddCollection(Collection c) {
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.CREATE));

        User user = AuthManager.getInstance().getUser();
        if (user != null) {
            user.total_collections ++;
            MessageBus.getInstance().post(user);
        }
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        MessageBus.getInstance().post(PhotoEvent.collectOrRemove(p, c));
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.UPDATE));
        MessageBus.getInstance().post(u);
    }
}
