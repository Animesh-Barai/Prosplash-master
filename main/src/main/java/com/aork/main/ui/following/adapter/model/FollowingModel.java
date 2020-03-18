package com.aork.main.ui.following.adapter.model;

import com.aork.base.unsplash.Photo;
import com.aork.common.base.adapter.BaseAdapter;

public abstract class FollowingModel implements BaseAdapter.ViewModel {

    public Photo photo;
    public int adapterPosition;
    public int photoPosition;

    public FollowingModel(Photo photo, int adapterPosition, int photoPosition) {
        this.photo = photo;
        this.adapterPosition = adapterPosition;
        this.photoPosition = photoPosition;
    }
}
