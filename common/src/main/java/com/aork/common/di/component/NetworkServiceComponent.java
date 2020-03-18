package com.aork.common.di.component;

import com.aork.common.di.module.NetworkServiceModule;
import com.aork.common.ui.dialog.DeleteCollectionPhotoDialog;
import com.aork.common.ui.dialog.ProfileDialog;
import com.aork.common.ui.dialog.SelectCollectionDialog;
import com.aork.common.utils.manager.AuthManager;
import com.aork.common.utils.manager.UserNotificationManager;

import dagger.Component;

@Component(modules = NetworkServiceModule.class)
public interface NetworkServiceComponent {

    void inject(UserNotificationManager manager);
    void inject(AuthManager manager);

    void inject(DeleteCollectionPhotoDialog dialog);
    void inject(SelectCollectionDialog dialog);
    void inject(ProfileDialog dialog);
}
