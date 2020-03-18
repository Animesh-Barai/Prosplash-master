package com.aork.photo;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.aork.common.base.application.MultiModulesApplication;
import com.aork.base.unsplash.Photo;
import com.aork.component.ComponentFactory;
import com.aork.component.module.PhotoModule;
import com.aork.photo.base.RoutingHelper;

public class PhotoApplication extends MultiModulesApplication {

    private class PhotoModuleIMP implements PhotoModule {

        @Override
        public void startPhotoActivity(Activity a, View image, View background, Photo photo, int currentIndex) {
            RoutingHelper.startPhotoActivity(a, image, background, photo, currentIndex);
        }

        @Override
        public void startPhotoActivity(Activity a, String photoId) {
            RoutingHelper.startPhotoActivity(a, photoId);
        }
    }

    @Override
    public void initModuleComponent(Context context) {
        ComponentFactory.setPhotoModule(new PhotoModuleIMP());
    }
}
