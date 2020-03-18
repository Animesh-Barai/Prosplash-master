package com.aork.me.ui.view;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;

import com.aork.base.i.PagerView;
import com.aork.common.presenter.pager.PagerScrollablePresenter;
import com.aork.base.i.PagerManageView;
import com.aork.common.ui.adapter.photo.PhotoAdapter;
import com.aork.common.ui.adapter.multipleState.MiniErrorStateAdapter;
import com.aork.common.ui.adapter.multipleState.MiniLoadingStateAdapter;
import com.aork.common.ui.decoration.GridMarginsItemDecoration;
import com.aork.common.ui.widget.MultipleStateRecyclerView;
import com.aork.common.ui.widget.insets.FitBottomSystemBarBothWaySwipeRefreshLayout;
import com.aork.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.aork.common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.aork.common.utils.BackToTopUtils;
import com.aork.common.utils.helper.RecyclerViewHelper;
import com.aork.common.utils.manager.ThemeManager;
import com.aork.common.presenter.pager.PagerStateManagePresenter;
import com.aork.me.R;
import com.aork.me.R2;
import com.aork.me.activity.MeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Me photo view.
 *
 * This view is used to show application user's photos.
 *
 * */

@SuppressLint("ViewConstructor")
public class MePhotosView extends FitBottomSystemBarBothWaySwipeRefreshLayout
        implements PagerView, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        MiniErrorStateAdapter.OnRetryListener {

    @BindView(R2.id.container_photo_list_recyclerView) MultipleStateRecyclerView recyclerView;

    private PagerStateManagePresenter stateManagePresenter;

    private int index;
    private PagerManageView pagerManageView;

    public MePhotosView(MeActivity a, PhotoAdapter adapter, int index, PagerManageView v) {
        super(a);
        this.init(adapter, index, v);
    }

    // init.

    @SuppressLint("InflateParams")
    private void init(PhotoAdapter adapter, int index, PagerManageView v) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.container_photo_list_2, null);
        addView(contentView);

        ButterKnife.bind(this, this);
        initData(index, v);
        initView(adapter);
    }

    private void initData(int index, PagerManageView v) {
        this.index = index;
        this.pagerManageView = v;
    }

    private void initView(PhotoAdapter adapter) {
        setColorSchemeColors(ThemeManager.getContentColor(getContext()));
        setProgressBackgroundColorSchemeColor(ThemeManager.getRootColor(getContext()));
        setOnRefreshAndLoadListener(this);
        setRefreshEnabled(false);
        setLoadEnabled(false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                RecyclerViewHelper.getDefaultStaggeredGridLayoutManager(getContext())
        );
        recyclerView.addItemDecoration(new GridMarginsItemDecoration(getContext(), recyclerView));

        recyclerView.setAdapter(new MiniLoadingStateAdapter(), MultipleStateRecyclerView.STATE_LOADING);
        recyclerView.setAdapter(new MiniErrorStateAdapter(this), MultipleStateRecyclerView.STATE_ERROR);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                PagerScrollablePresenter.onScrolled(
                        MePhotosView.this,
                        recyclerView,
                        adapter.getItemCount(),
                        pagerManageView,
                        index,
                        dy
                );
            }
        });
        recyclerView.setState(MultipleStateRecyclerView.STATE_LOADING);

        stateManagePresenter = new PagerStateManagePresenter(recyclerView);
    }

    // interface.

    // pager view.

    @Override
    public State getState() {
        return stateManagePresenter.getState();
    }

    @Override
    public boolean setState(State state) {
        return stateManagePresenter.setState(state);
    }

    @Override
    public void setSelected(boolean selected) {
    }

    @Override
    public void setSwipeRefreshing(boolean refreshing) {
        setRefreshing(refreshing);
    }

    @Override
    public void setSwipeLoading(boolean loading) {
        setLoading(loading);
    }

    @Override
    public void setPermitSwipeRefreshing(boolean permit) {
        // do nothing.
    }

    @Override
    public void setPermitSwipeLoading(boolean permit) {
        setLoadEnabled(permit);
    }

    @Override
    public boolean checkNeedBackToTop() {
        return recyclerView.canScrollVertically(-1)
                && stateManagePresenter.getState() == State.NORMAL;
    }

    @Override
    public void scrollToPageTop() {
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public boolean canSwipeBack(int dir) {
        return stateManagePresenter.getState() != State.NORMAL
                || SwipeBackCoordinatorLayout.canSwipeBack(recyclerView, dir);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    // on refresh an load listener.

    @Override
    public void onRefresh() {
        pagerManageView.onRefresh(index);
    }

    @Override
    public void onLoad() {
        pagerManageView.onLoad(index);
    }

    // on retry listener.

    @Override
    public void onRetry() {
        pagerManageView.onRefresh(index);
    }
}