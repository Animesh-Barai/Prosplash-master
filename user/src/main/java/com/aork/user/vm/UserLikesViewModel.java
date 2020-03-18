package com.aork.user.vm;

import android.text.TextUtils;

import com.aork.user.repository.UserPhotosViewRepository;

import javax.inject.Inject;

public class UserLikesViewModel extends UserPhotosViewModel {

    @Inject
    public UserLikesViewModel(UserPhotosViewRepository repository) {
        super(repository);
    }

    @Override
    public void refresh() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserLikes(this, getUsername(), true);
    }

    @Override
    public void load() {
        if (TextUtils.isEmpty(getUsername())) {
            return;
        }
        getRepository().getUserLikes(this, getUsername(), false);
    }
}
