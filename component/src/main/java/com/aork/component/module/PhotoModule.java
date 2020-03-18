package com.aork.component.module;

import android.app.Activity;
import android.view.View;

import com.aork.base.unsplash.Photo;

public interface PhotoModule {

    void startPhotoActivity(Activity a, View image, View background, Photo photo, int currentIndex);

    void startPhotoActivity(Activity a, String photoId);
}
