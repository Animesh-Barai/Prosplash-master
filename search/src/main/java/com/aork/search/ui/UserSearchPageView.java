package com.aork.search.ui;

import android.annotation.SuppressLint;

import com.aork.base.i.PagerManageView;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.common.utils.helper.RecyclerViewHelper;
import com.aork.search.R;
import com.aork.search.SearchActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressLint("ViewConstructor")
public class UserSearchPageView extends AbstractSearchPageView {

    public UserSearchPageView(SearchActivity a, BaseAdapter adapter,
                              boolean selected, int index, PagerManageView v) {
        super(a, adapter, selected, index, v);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(
                getContext(),
                RecyclerViewHelper.getGirdColumnCount(getContext())
        );
    }

    @Override
    protected String getInitFeedbackText() {
        return getContext().getString(R.string.feedback_search_users_tv);
    }
}
