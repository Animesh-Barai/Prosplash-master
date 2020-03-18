package com.aork.component.module;

import android.app.Activity;
import android.view.View;

import com.aork.base.unsplash.Collection;

public interface CollectionModule {

    void startCollectionActivity(Activity a,
                                 View avatar, View background, Collection c);

    void startCollectionActivity(Activity a, Collection c);

    void startCollectionActivity(Activity a, String collectionId);
}
