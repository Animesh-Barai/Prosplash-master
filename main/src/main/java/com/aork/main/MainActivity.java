package com.aork.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.aork.base.i.Downloadable;
import com.aork.base.i.PagerManageView;
import com.aork.base.i.PagerView;
import com.aork.base.pager.ListPager;
import com.aork.base.resource.ListResource;
import com.aork.common.base.activity.LoadableActivity;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.base.DownloadTask;
import com.aork.base.pager.ProfilePager;
import com.aork.base.resource.Resource;
import com.aork.base.unsplash.Photo;
import com.aork.common.base.popup.MysplashPopupWindow;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.base.vm.pager.PagerViewModel;
import com.aork.common.image.ImageHelper;
import com.aork.common.presenter.LoadImagePresenter;
import com.aork.common.presenter.TabLayoutDoubleClickBackToTopPresenter;
import com.aork.common.presenter.pager.PagerLoadablePresenter;
import com.aork.common.presenter.pager.PagerViewManagePresenter;
import com.aork.common.ui.adapter.PagerAdapter;
import com.aork.common.ui.adapter.collection.CollectionAdapter;
import com.aork.common.ui.adapter.collection.CollectionItemEventHelper;
import com.aork.common.ui.adapter.photo.PhotoAdapter;
import com.aork.common.ui.adapter.photo.PhotoItemEventHelper;
import com.aork.common.ui.widget.AutoHideInkPageIndicator;
import com.aork.common.ui.widget.CircularImageView;
import com.aork.common.ui.widget.NestedScrollAppBarLayout;
import com.aork.common.ui.widget.insets.FitBottomSystemBarViewPager;
import com.aork.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.aork.common.utils.AnimUtils;
import com.aork.common.utils.DisplayUtils;
import com.aork.common.utils.helper.NotificationHelper;
import com.aork.common.utils.manager.AuthManager;
import com.aork.common.utils.BackToTopUtils;
import com.aork.common.utils.manager.ThemeManager;
import com.aork.component.ComponentFactory;
import com.aork.main.di.component.DaggerApplicationComponent;
import com.aork.main.presenter.FollowingFeedViewManagePresenter;
import com.aork.main.ui.CollectionsHomePageView;
import com.aork.main.ui.PhotosHomePageView;
import com.aork.main.ui.following.FollowingHomePageView;
import com.aork.main.ui.following.adapter.FollowingAdapter;
import com.aork.main.ui.following.adapter.FollowingItemEventHelper;
import com.aork.main.vm.CollectionsHomePageViewModel;
import com.aork.main.vm.FollowingHomePageViewModel;
import com.aork.main.vm.MainActivityModel;
import com.aork.main.vm.PhotosHomePageViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.nekocode.rxlifecycle.LifecycleEvent;
import cn.nekocode.rxlifecycle.compact.RxLifecycleCompact;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Main activity.
 */

public class MainActivity extends LoadableActivity<Photo>
        implements PagerManageView, ViewPager.OnPageChangeListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener {

    @BindView(R2.id.activity_main_container)
    CoordinatorLayout container;

    @BindView(R2.id.activity_main_appBar)
    NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_main_searchBar)
    CardView searchBar;

    @OnClick(R2.id.activity_main_searchBar)
    void doSearch() {
        ComponentFactory.getSearchModule().startSearchActivity(this, searchBar, null);
    }

    @BindView(R2.id.activity_main_avatar)
    CircularImageView avatar;

    @OnClick(R2.id.activity_main_avatar)
    void showProfile() {
        ComponentFactory.getMeModule().startMeActivity(
                this, avatar, searchBar, ProfilePager.PAGE_PHOTO);
    }

    @BindView(R2.id.activity_main_menuButton)
    ImageButton menuButton;
    @OnClick(R2.id.activity_main_menuButton) void showDrawer() {
        int menuId = AuthManager.getInstance().isAuthorized()
                ? R.menu.activity_main_toolbar_auth
                : R.menu.activity_main_toolbar;
        MysplashPopupWindow.show(this, menuButton, menuId, item -> {
            int id = item.getItemId();
            if (id == R.id.action_change_theme) {
                changeTheme();
            } else if (id == R.id.action_download_manage) {
                ComponentFactory.getDownloaderService().startDownloadManageActivity(this);
            } else if (id == R.id.action_settings) {
                ComponentFactory.getSettingsService().startSettingsActivity(this);
            } else if (id == R.id.action_about) {
                ComponentFactory.getAboutModule().startAboutActivity(this);
            } else if (id == R.id.action_logout) {
                AuthManager.getInstance().logout();
                for (int i = 0; i < pageCount(); i ++) {
                    pagerModels[i].refresh();
                }
            }
            return true;
        });
    }

    @BindView(R2.id.activity_main_searchHint)
    TextView searchHint;

    @BindView(R2.id.activity_main_logo)
    LinearLayout logo;
    @BindView(R2.id.activity_main_appIcon)
    ImageView appIcon;

    @BindView(R2.id.activity_main_viewPager)
    FitBottomSystemBarViewPager viewPager;
    @BindView(R2.id.activity_main_indicator)
    AutoHideInkPageIndicator indicator;

    private PagerView[] pagers = new PagerView[pageCount()];
    private RecyclerView.Adapter[] adapters = new RecyclerView.Adapter[pageCount()];

    private MainActivityModel mainActivityModel;
    private PagerViewModel[] pagerModels = new PagerViewModel[pageCount()];
    @Inject
    ParamsViewModelFactory viewModelFactory;

    public static final String MAIN_ACTIVITY = "/main/MainActivity";
    //Image button to the bottomsheet dialog inicializtation
    ImageButton btnbottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initModel();
        initView();
        ComponentFactory.getAboutModule().checkAndStartIntroduce(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mainActivityModel.checkToRequestAuthInformation();

        AnimUtils.animShow(logo, 300, logo.getAlpha(), 1);
        AnimUtils.animHide(searchHint, 300, searchHint.getAlpha(), 0, true);

        Observable.timer(5, TimeUnit.SECONDS)
                .compose(RxLifecycleCompact.bind(this).disposeObservableWhen(LifecycleEvent.PAUSE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    AnimUtils.animShow(searchHint);
                    AnimUtils.animHide(logo);
                }).subscribe();
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
        ensureFollowingAvatar();
        pagers[getCurrentPagerPosition()].scrollToPageTop();
    }

    @Override
    public void finishSelf(boolean backPressed) {
        finish();
    }

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return null;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Photo> loadMoreData(int currentCount) {
        if (getCurrentPagerPosition() == photoPage() || getCurrentPagerPosition() == followingPage()) {
            int page = getCurrentPagerPosition();
            return PagerLoadablePresenter.loadMore(
                    (PagerViewModel<Photo>) pagerModels[page],
                    currentCount,
                    pagers[page],
                    pagers[page].getRecyclerView(),
                    this,
                    page
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
        mainActivityModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityModel.class);
        if (!AuthManager.getInstance().isAuthorized() || AuthManager.getInstance().getUser() == null) {
            mainActivityModel.init(Resource.error(null), photoPage());
        } else {
            mainActivityModel.init(Resource.success(AuthManager.getInstance().getUser()), photoPage());
        }

        pagerModels[photoPage()] = ViewModelProviders.of(this, viewModelFactory).get(
                PhotosHomePageViewModel.class);
        ((PhotosHomePageViewModel) pagerModels[photoPage()]).init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));

        pagerModels[followingPage()] = ViewModelProviders.of(this, viewModelFactory).get(
                FollowingHomePageViewModel.class);
        ((FollowingHomePageViewModel) pagerModels[followingPage()]).init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));

        pagerModels[collectionPage()] = ViewModelProviders.of(this, viewModelFactory).get(
                CollectionsHomePageViewModel.class);
        ((CollectionsHomePageViewModel) pagerModels[collectionPage()]).init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE));
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        appBar.setOnNestedScrollingListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchBar.setTransitionName(getClass().getSimpleName() + "-searchBar");
            avatar.setTransitionName(getClass().getSimpleName() + "-avatar");
        }

        mainActivityModel.getUserResource().observe(this, state -> LoadImagePresenter.loadUserAvatar(
                this, avatar, AuthManager.getInstance().getUser(), null));

        ImageHelper.loadImage(this, appIcon, R.drawable.ic_launcher);

        initPages();
    }

    @SuppressWarnings("unchecked")
    private void initPages() {
        pagerModels[photoPage()].readDataList(list ->
                adapters[photoPage()] = new PhotoAdapter(this, list).setItemEventCallback(
                        new PhotoItemEventHelper(
                                this,
                                pagerModels[photoPage()],
                                (context, photo) -> downloadPhoto(photo)
                        )
                )
        );

        pagerModels[followingPage()].readDataList(list ->
                adapters[followingPage()] = new FollowingAdapter(
                        this,
                        list,
                        new FollowingItemEventHelper(
                                this,
                                (FollowingHomePageViewModel) pagerModels[followingPage()],
                                (context, photo) -> downloadPhoto(photo)
                        )
                )
        );

        pagerModels[collectionPage()].readDataList(list ->
                adapters[collectionPage()] = new CollectionAdapter(this, list).setItemEventCallback(
                        new CollectionItemEventHelper(this)
                )
        );

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new PhotosHomePageView(
                                this,
                                (BaseAdapter) adapters[photoPage()],
                                getCurrentPagerPosition() == photoPage(),
                                photoPage(),
                                this
                        ), new FollowingHomePageView(
                                this,
                                (FollowingAdapter) adapters[followingPage()],
                                getCurrentPagerPosition() == followingPage(),
                                followingPage(),
                                this
                        ), new CollectionsHomePageView(
                                this,
                                (BaseAdapter) adapters[collectionPage()],
                                getCurrentPagerPosition() == collectionPage(),
                                collectionPage(),
                                this
                        )
                )
        );
        for (int i = 0; i < pageList.size(); i++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] searchTabs = getResources().getStringArray(R.array.home_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, searchTabs);
        PagerAdapter adapter = new PagerAdapter(viewPager, pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = findViewById(R.id.activity_main_tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayoutDoubleClickBackToTopPresenter(MainActivity.this::backToTop));

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        appBar.post(() ->
                ((FollowingHomePageView) pagers[followingPage()]).setOffsetY(
                        appBar.getY(), appBar.getMeasuredHeight())
        );

        mainActivityModel.getPagerPosition().observe(this, position -> {
            for (int i = photoPage(); i < pageCount(); i++) {
                pagers[i].setSelected(i == position);
            }

            if (pagerModels[position].getListSize() == 0
                    && pagerModels[position].getListState() != ListResource.State.REFRESHING
                    && pagerModels[position].getListState() != ListResource.State.LOADING
                    && pagerModels[position].getListState() != ListResource.State.ALL_LOADED) {
                if (position == followingPage()) {
                    FollowingFeedViewManagePresenter.initRefresh(
                            pagerModels[position], (FollowingAdapter) adapters[position]);
                } else {
                    PagerViewManagePresenter.initRefresh(
                            pagerModels[position], (BaseAdapter) adapters[position]);
                }
            }
        });

        for (int i = photoPage(); i < pageCount(); i++) {
            int finalI = i;
            if (finalI == followingPage()) {
                pagerModels[i].observeListResource(this, viewModel ->
                        FollowingFeedViewManagePresenter.responsePagerListResourceChanged(
                                viewModel, pagers[finalI], (FollowingAdapter) adapters[finalI]
                        )
                );
            } else {
                pagerModels[i].observeListResource(this, viewModel ->
                        PagerViewManagePresenter.responsePagerListResourceChanged(
                                viewModel, pagers[finalI], (BaseAdapter) adapters[finalI]
                        )
                );
            }
        }
    }

    // control.

    private int getCurrentPagerPosition() {
        if (mainActivityModel.getPagerPosition().getValue() == null) {
            return photoPage();
        } else {
            return mainActivityModel.getPagerPosition().getValue();
        }
    }

    private static int photoPage() {
        return 0;
    }

    private static int followingPage() {
        return 1;
    }

    private static int collectionPage() {
        return 2;
    }

    private static int pageCount() {
        return 3;
    }

    private void ensureFollowingAvatar() {
        if (appBar.getY() < 0) {
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ((FollowingHomePageView) pagers[followingPage()]).setOffsetY(
                            appBar.getY(), appBar.getMeasuredHeight());
                }
            };
            a.setDuration(300);

            ((FollowingHomePageView) pagers[followingPage()]).clearAnimation();
            ((FollowingHomePageView) pagers[followingPage()]).startAnimation(a);
        }
    }

    public void downloadPhoto(Photo photo) {
        requestReadWritePermission(photo, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                ComponentFactory.getDownloaderService().addTask(
                        MainActivity.this,
                        (Photo) downloadable,
                        DownloadTask.DOWNLOAD_TYPE,
                        ComponentFactory.getSettingsService().getDownloadScale()
                );
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        MainActivity.this, getString(R.string.feedback_need_permission));
            }
        });
    }

    private void changeTheme() {
        DisplayUtils.changeTheme((Context) this);
        AppCompatDelegate.setDefaultNightMode(
                ThemeManager.getInstance(this).isLightTheme()
                        ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES
        );
        recreate();
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

    // on page change listener.

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        mainActivityModel.setPagerPosition(position);
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
        ((FollowingHomePageView) pagers[followingPage()]).setOffsetY(appBar.getY(), appBar.getMeasuredHeight());
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }
}