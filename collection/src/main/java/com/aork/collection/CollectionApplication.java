package com.aork.collection;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.aork.collection.base.RoutingHelper;
import com.aork.common.base.application.MultiModulesApplication;
import com.aork.base.unsplash.Collection;
import com.aork.component.ComponentFactory;
import com.aork.component.module.CollectionModule;

public class CollectionApplication extends MultiModulesApplication {

    private class CollectionModuleIMP implements CollectionModule {

        @Override
        public void startCollectionActivity(Activity a,
                                            View avatar, View background, Collection c) {
            RoutingHelper.startCollectionActivity(a, avatar, background, c);
        }

        @Override
        public void startCollectionActivity(Activity a, Collection c) {
            RoutingHelper.startCollectionActivity(a, c);
        }

        @Override
        public void startCollectionActivity(Activity a, String collectionId) {
            RoutingHelper.startCollectionActivity(a, collectionId);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setCollectionModule(new CollectionModuleIMP());
    }
}
