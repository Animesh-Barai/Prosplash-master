package com.aork.photo.ui.adapter.photo.model;

import com.aork.base.i.Tag;
import com.aork.base.unsplash.Photo;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.photo.ui.adapter.photo.PhotoInfoAdapter3;

import java.util.ArrayList;
import java.util.List;

public class TagModel extends PhotoInfoAdapter3.ViewModel {

    public List<Tag> list;
    public int scrollX;

    public TagModel(Photo photo, int scrollX) {
        super(photo);

        this.list = new ArrayList<>();
        if (photo.tags != null) {
            list.addAll(photo.tags);
        }

        this.scrollX = scrollX;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof TagModel;
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        if (((TagModel) newModel).list.size() != list.size()
                || ((TagModel) newModel).scrollX != scrollX) {
            return false;
        }

        for (int i = 0; i < list.size(); i ++) {
            if (!((TagModel) newModel).list.get(i).getTitle().equals(list.get(i).getTitle())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        return null;
    }
}
