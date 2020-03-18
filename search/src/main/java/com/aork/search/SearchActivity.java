package com.aork.search;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aork.base.DownloadTask;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.common.base.application.MysplashApplication;
import com.aork.base.pager.ListPager;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.base.vm.pager.PagerViewModel;
import com.aork.common.presenter.TabLayoutDoubleClickBackToTopPresenter;
import com.aork.common.ui.adapter.collection.CollectionItemEventHelper;
import com.aork.common.ui.adapter.photo.PhotoItemEventHelper;
import com.aork.common.ui.adapter.user.UserItemEventHelper;
import com.aork.common.ui.widget.insets.FitBottomSystemBarViewPager;
import com.aork.common.utils.AnimUtils;
import com.aork.component.ComponentFactory;
import com.aork.base.resource.ListResource;
import com.aork.base.i.PagerView;
import com.aork.common.ui.adapter.collection.CollectionAdapter;
import com.aork.common.ui.adapter.user.UserAdapter;
import com.aork.common.presenter.pager.PagerLoadablePresenter;
import com.aork.base.i.PagerManageView;
import com.aork.common.base.activity.LoadableActivity;
import com.aork.base.unsplash.Photo;
import com.aork.common.ui.adapter.PagerAdapter;
import com.aork.common.ui.adapter.photo.PhotoAdapter;
import com.aork.common.ui.widget.AutoHideInkPageIndicator;
import com.aork.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.aork.common.ui.widget.NestedScrollAppBarLayout;
import com.aork.common.utils.BackToTopUtils;
import com.aork.common.utils.DisplayUtils;
import com.aork.common.utils.manager.ThemeManager;
import com.aork.common.presenter.pager.PagerViewManagePresenter;
import com.aork.search.di.component.DaggerApplicationComponent;
import com.aork.search.ui.CollectionSearchPageView;
import com.aork.search.ui.PhotoSearchPageView;
import com.aork.search.ui.UserSearchPageView;
import com.aork.search.vm.CollectionSearchPageViewModel;
import com.aork.search.vm.PhotoSearchPageViewModel;
import com.aork.search.vm.SearchActivityModel;
import com.aork.search.vm.SearchPagerViewModel;
import com.aork.search.vm.UserSearchPageViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.compact.RxLifecycleCompact;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Search activity.
 * 
 * This activity is used to search something.
 * 
 * */

@Route(path = SearchActivity.SEARCH_ACTIVITY)
public class SearchActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener, EditText.OnEditorActionListener,
        ViewPager.OnPageChangeListener, NestedScrollAppBarLayout.OnNestedScrollingListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_search_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_search_container) CoordinatorLayout container;
    @BindView(R2.id.activity_search_shadow) View shadow;

    @BindView(R2.id.activity_search_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_search_editText) EditText editText;

    @BindView(R2.id.activity_search_viewPager) FitBottomSystemBarViewPager viewPager;
    @BindView(R2.id.activity_search_indicator) AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[pageCount()];
    private BaseAdapter[] adapters = new BaseAdapter[pageCount()];

    private SearchActivityModel activityModel;
    private PagerViewModel[] pagerModels = new PagerViewModel[pageCount()];
    @Inject ParamsViewModelFactory viewModelFactory;

    private boolean executeTransition;

    public static final String SEARCH_ACTIVITY = "/search/SearchActivity";
    public static final String ACTION_SEARCH_ACTIVITY = "com.wangdaye.mysplash.Search";
    public static final String KEY_SEARCH_ACTIVITY_QUERY = "search_activity_query";
    public static final String KEY_EXECUTE_TRANSITION = "execute_transition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            executeTransition = getIntent().getBooleanExtra(KEY_EXECUTE_TRANSITION, false);
        } else {
            executeTransition = false;
        }

        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().post(() -> {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                showKeyboard();
            } else {
                hideKeyboard();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyboard();
    }

    @Override
    public void handleBackPressed() {
        if (pagers[getCurrentPagerPosition()].checkNeedBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishSelf(true);
        }
    }

    @Override
    protected void backToTop() {
        BackToTopUtils.showTopBar(appBar, viewPager);
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        if (executeTransition) {
            appBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            finishAfterTransition();
        } else {
            finish();
            if (backPressed) {
                // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
            } else {
                overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
            }
        }
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return swipeBackView;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Photo> loadMoreData(int currentCount) {
        if (getCurrentPagerPosition() == photoPage()) {
            return PagerLoadablePresenter.loadMore(
                    (PagerViewModel<Photo>) pagerModels[photoPage()],
                    currentCount,
                    pagers[photoPage()],
                    pagers[photoPage()].getRecyclerView(),
                    this,
                    photoPage()
            );
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isValidProvider(Class clazz) {
        return clazz == Photo.class;
    }

    // init.

    private void initModel() {
        String query = getIntent().getStringExtra(KEY_SEARCH_ACTIVITY_QUERY);
        if (query == null) {
            query = "";
        }

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(SearchActivityModel.class);
        activityModel.init(photoPage(), query);

        pagerModels[photoPage()] = ViewModelProviders.of(this, viewModelFactory).get(
                PhotoSearchPageViewModel.class);
        ((PhotoSearchPageViewModel) pagerModels[photoPage()]).init(
                ListResource.error(0, ListPager.DEFAULT_PER_PAGE), query);

        pagerModels[collectionPage()] = ViewModelProviders.of(this, viewModelFactory).get(
                CollectionSearchPageViewModel.class);
        ((CollectionSearchPageViewModel) pagerModels[collectionPage()]).init(
                ListResource.error(0, ListPager.DEFAULT_PER_PAGE), query);

        pagerModels[userPage()] = ViewModelProviders.of(this, viewModelFactory).get(
                UserSearchPageViewModel.class);
        ((UserSearchPageViewModel) pagerModels[userPage()]).init(
                ListResource.error(0, ListPager.DEFAULT_PER_PAGE), query);
    }

    @SuppressWarnings("unchecked")
    private void initView() {
        swipeBackView.setOnSwipeListener(this);

        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = findViewById(R.id.activity_search_toolbar);
        if (MysplashApplication.getInstance().getActivityCount() == 1) {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar,
                    R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        DisplayUtils.inflateToolbarMenu(toolbar, R.menu.activity_search_toolbar, this);
        toolbar.setNavigationOnClickListener(v -> {
            if (MysplashApplication.getInstance().getActivityCount() == 1) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });

        editText.setOnEditorActionListener(this);

        initPages();

        activityModel.getSearchQuery().observe(this, s -> {
            if (!TextUtils.equals(s, editText.getText().toString())) {
                editText.setText(s);
            }
            for (int i = photoPage(); i < pageCount(); i ++) {
                SearchPagerViewModel viewModel = (SearchPagerViewModel) pagerModels[i];
                if (!TextUtils.equals(s, viewModel.getQuery())) {
                    viewModel.setQuery(s);
                    PagerViewManagePresenter.initRefresh(pagerModels[i], adapters[i]);
                }
            }
        });

        if (executeTransition) {
            appBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            Observable.timer(350, TimeUnit.MILLISECONDS)
                    .compose(RxLifecycleCompact.bind(this).disposeObservableWhen(LifecycleEvent.DESTROY))
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        AnimUtils.animShow(appBar);
                        AnimUtils.animShow(viewPager);
                    }).subscribe();
        }
    }

    @SuppressWarnings("unchecked")
    private void initPages() {
        pagerModels[photoPage()].readDataList(list ->
                adapters[photoPage()] = new PhotoAdapter(this, list).setItemEventCallback(
                        new PhotoItemEventHelper(
                                this,
                                pagerModels[photoPage()],
                                (context, photo) -> ComponentFactory.getDownloaderService().addTask(
                                        this,
                                        photo,
                                        DownloadTask.DOWNLOAD_TYPE,
                                        ComponentFactory.getSettingsService().getDownloadScale()
                                )
                        )
                )
        );

        pagerModels[collectionPage()].readDataList(list ->
                adapters[collectionPage()] = new CollectionAdapter(this, list).setItemEventCallback(
                        new CollectionItemEventHelper(this)
                )
        );

        pagerModels[userPage()].readDataList(list ->
                adapters[userPage()] = new UserAdapter(this, list).setItemEventCallback(
                        new UserItemEventHelper(this)
                )
        );

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new PhotoSearchPageView(
                                this,
                                adapters[photoPage()],
                                getCurrentPagerPosition() == photoPage(),
                                photoPage(),
                                this
                        ).setOnClickListenerForFeedbackView(v -> hideKeyboard()),
                        new CollectionSearchPageView(
                                this,
                                adapters[collectionPage()],
                                getCurrentPagerPosition() == collectionPage(),
                                collectionPage(),
                                this
                        ).setOnClickListenerForFeedbackView(v -> hideKeyboard()),
                        new UserSearchPageView(
                                this,
                                adapters[userPage()],
                                getCurrentPagerPosition() == userPage(),
                                userPage(),
                                this
                        ).setOnClickListenerForFeedbackView(v -> hideKeyboard())
                )
        );
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] searchTabs = getResources().getStringArray(R.array.search_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, searchTabs);
        PagerAdapter adapter = new PagerAdapter(viewPager, pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = findViewById(R.id.activity_search_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayoutDoubleClickBackToTopPresenter(SearchActivity.this::backToTop));

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        activityModel.getPagerPosition().observe(this, position -> {
            for (int i = photoPage(); i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }

            String query = ((SearchPagerViewModel) pagerModels[getCurrentPagerPosition()]).getQuery();
            if (pagerModels[getCurrentPagerPosition()].getListSize() == 0
                    && pagerModels[getCurrentPagerPosition()].getListState() != ListResource.State.REFRESHING
                    && pagerModels[getCurrentPagerPosition()].getListState() != ListResource.State.LOADING
                    && pagerModels[getCurrentPagerPosition()].getListState() != ListResource.State.ALL_LOADED
                    && !TextUtils.isEmpty(query)) {
                PagerViewManagePresenter.initRefresh(
                        pagerModels[getCurrentPagerPosition()],
                        adapters[getCurrentPagerPosition()]
                );
            }
        });

        for (int i = photoPage(); i < pageCount(); i ++) {
            int finalI = i;
            pagerModels[i].observeListResource(this, viewModel ->
                    PagerViewManagePresenter.responsePagerListResourceChanged(
                            viewModel, pagers[finalI], adapters[finalI]
                    )
            );
        }
    }

    // control.

    private int getCurrentPagerPosition() {
        if (activityModel.getPagerPosition().getValue() == null) {
            return photoPage();
        } else {
            return activityModel.getPagerPosition().getValue();
        }
    }

    private static int photoPage() {
        return 0;
    }

    private static int collectionPage() {
        return 1;
    }

    private static int userPage() {
        return 2;
    }

    private static int pageCount() {
        return 3;
    }

    private void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null) {
            editText.setFocusable(true);
            editText.requestFocus();
            manager.showSoftInput(editText, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            editText.clearFocus();
        }
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        pagerModels[index].refresh();
    }

    @Override
    public void onLoad(int index) {
        pagerModels[index].load();
    }

    @Override
    public boolean canLoadMore(int index) {
        return pagerModels[index].getListState() != ListResource.State.REFRESHING
                && pagerModels[index].getListState() != ListResource.State.LOADING
                && pagerModels[index].getListState() != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return pagerModels[index].getListState() == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_text) {
            editText.setText("");
            showKeyboard();
        }
        return true;
    }

    // on editor action listener.

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        String text = textView.getText().toString();
        if (!TextUtils.isEmpty(text)
                && !TextUtils.equals(activityModel.getSearchQuery().getValue(), text)) {
            activityModel.setSearchQuery(text);
            hideKeyboard();
        }
        return true;
    }

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        activityModel.setPagerPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (appBar.getY() <= -appBar.getMeasuredHeight()) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    indicator.setDisplayState(true);
                    break;

                case ViewPager.SCROLL_STATE_IDLE:
                    indicator.setDisplayState(false);
                    break;
            }
        }
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null && manager.isActive(editText)) {
            hideKeyboard();
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on swipe listener.(swipe back listener)

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight();
        } else {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() >= 0;
        }
    }

    @Override
    public void onSwipeProcess(@SwipeBackCoordinatorLayout.DirectionRule int dir, float percent) {
        shadow.setAlpha(SwipeBackCoordinatorLayout.getBackgroundAlpha(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
