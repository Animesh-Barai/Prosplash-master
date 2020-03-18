package com.aork.main.ui;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import com.aork.common.base.adapter.BaseAdapter;
import com.aork.main.MainActivity;
import com.aork.main.R;
import com.aork.base.i.PagerManageView;
import com.aork.common.utils.helper.RecyclerViewHelper;

@SuppressLint("ViewConstructor")
public class PhotosHomePageView extends AbstractHomePageView {

    public PhotosHomePageView(MainActivity a, BaseAdapter adapter,
                              boolean selected, int index, PagerManageView v) {
        super(a, adapter, selected, index, v);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return RecyclerViewHelper.getDefaultStaggeredGridLayoutManager(getContext());
    }

    @Override
    protected String getFeedbackText() {
        return getContext().getString(R.string.feedback_load_failed_tv);
    }

    @Override
    protected String getFeedbackButton() {
        return getContext().getString(R.string.feedback_click_retry);
    }
}
