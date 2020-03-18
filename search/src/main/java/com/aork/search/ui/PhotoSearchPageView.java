package com.aork.search.ui;

import android.annotation.SuppressLint;

import com.aork.base.i.PagerManageView;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.common.utils.helper.RecyclerViewHelper;
import com.aork.search.R;
import com.aork.search.SearchActivity;

import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class PhotoSearchPageView extends AbstractSearchPageView {

    public PhotoSearchPageView(SearchActivity a, BaseAdapter adapter,
                               boolean selected, int index, PagerManageView v) {
        super(a, adapter, selected, index, v);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return RecyclerViewHelper.getDefaultStaggeredGridLayoutManager(getContext());
    }

    @Override
    protected String getInitFeedbackText() {
        return getContext().getString(R.string.feedback_search_photos_tv);
    }
}
