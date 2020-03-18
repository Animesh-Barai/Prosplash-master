package com.aork.me.ui.adapter;

import android.view.View;

import com.aork.base.pager.ProfilePager;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.base.unsplash.User;
import com.aork.common.presenter.FollowUserPresenter;
import com.aork.component.ComponentFactory;

public class MyFollowItemEventHelper implements MyFollowAdapter.ItemEventCallback {

    private MysplashActivity activity;

    public MyFollowItemEventHelper(MysplashActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onFollowItemClicked(View avatar, View background, User user) {
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, user, ProfilePager.PAGE_PHOTO);
    }

    @Override
    public void onFollowUserOrCancel(User user, int adapterPosition, boolean follow) {
        if (follow) {
            FollowUserPresenter.getInstance().follow(user);
        } else {
            FollowUserPresenter.getInstance().unfollow(user);
        }
    }
}
