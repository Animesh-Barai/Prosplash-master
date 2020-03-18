package com.aork.me.ui.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.aork.base.unsplash.User;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.common.image.ImageHelper;
import com.aork.common.presenter.FollowUserPresenter;

class MyFollowModel implements BaseAdapter.ViewModel {

    @Nullable String avatarUrl;
    @Size int[] avatarSize;

    String title;

    boolean progressing;
    boolean switchOn;

    User user;

    MyFollowModel(User user) {
        if (TextUtils.isEmpty(user.profile_image.large)) {
            avatarUrl = null;
        } else {
            avatarUrl = user.profile_image.large;
        }
        avatarSize = new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE};

        title = user.name;

        progressing = FollowUserPresenter.getInstance().isInProgress(user);
        switchOn = user.followed_by_user;

        this.user = user;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof MyFollowModel
                && ((MyFollowModel) newModel).user.username.equals(user.username);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        return ImageHelper.isSameUrl(((MyFollowModel) newModel).avatarUrl, avatarUrl)
                && ((MyFollowModel) newModel).title.equals(title)
                && ((MyFollowModel) newModel).progressing == progressing
                && ((MyFollowModel) newModel).switchOn == switchOn;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        if (ImageHelper.isSameUrl(((MyFollowModel) newModel).avatarUrl, avatarUrl)) {
            return 1;
        }
        return null;
    }
}
