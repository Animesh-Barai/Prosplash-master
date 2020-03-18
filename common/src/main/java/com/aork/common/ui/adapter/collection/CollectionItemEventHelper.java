package com.aork.common.ui.adapter.collection;

import android.view.View;

import com.aork.common.base.activity.MysplashActivity;
import com.aork.base.pager.ProfilePager;
import com.aork.base.unsplash.Collection;
import com.aork.base.unsplash.User;
import com.aork.component.ComponentFactory;

public class CollectionItemEventHelper implements CollectionAdapter.ItemEventCallback {

    private MysplashActivity activity;

    public CollectionItemEventHelper(MysplashActivity a) {
        activity = a;
    }

    @Override
    public void onCollectionClicked(View avatar, View background, Collection c) {
        ComponentFactory.getCollectionModule()
                .startCollectionActivity(activity, avatar, background, c);
    }

    @Override
    public void onUserClicked(View avatar, View background, User u) {
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, u, ProfilePager.PAGE_PHOTO);
    }
}
