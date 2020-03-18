package com.aork.about.model;

import com.aork.about.ui.AboutAdapter;
import com.aork.common.base.adapter.BaseAdapter;

import androidx.annotation.IntDef;

/**
 * About model.
 *
 * Item model for {@link AboutAdapter}.
 *
 * */

public interface AboutModel extends BaseAdapter.ViewModel {

    @AboutTypeRule
    int getType();

    int TYPE_HEADER = 1;
    int TYPE_CATEGORY = 2;
    int TYPE_APP = 3;
    int TYPE_TRANSLATOR = 4;
    int TYPE_LIBRARY = 5;
    @IntDef({TYPE_HEADER, TYPE_CATEGORY, TYPE_APP, TYPE_TRANSLATOR, TYPE_LIBRARY})
    @interface AboutTypeRule {}
}
