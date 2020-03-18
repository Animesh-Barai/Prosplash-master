package com.aork.user;

import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aork.base.i.Downloadable;
import com.aork.base.pager.ListPager;
import com.aork.base.pager.ProfilePager;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.base.vm.pager.PagerViewModel;
import com.aork.common.presenter.LoadImagePresenter;
import com.aork.common.presenter.TabLayoutDoubleClickBackToTopPresenter;
import com.aork.common.ui.adapter.photo.PhotoItemEventHelper;
import com.aork.common.ui.dialog.ProfileDialog;
import com.aork.common.ui.widget.insets.FitBottomSystemBarViewPager;
import com.aork.common.utils.helper.RoutingHelper;
import com.aork.common.ui.adapter.collection.CollectionItemEventHelper;
import com.aork.component.ComponentFactory;
import com.aork.base.DownloadTask;
import com.aork.base.resource.ListResource;
import com.aork.base.i.PagerView;
import com.aork.base.resource.Resource;
import com.aork.common.base.vm.PagerManageViewModel;
import com.aork.common.ui.adapter.collection.CollectionAdapter;
import com.aork.common.presenter.BrowsableDialogMangePresenter;
import com.aork.common.presenter.pager.PagerLoadablePresenter;
import com.aork.base.i.PagerManageView;
import com.aork.common.base.activity.LoadableActivity;
import com.aork.base.unsplash.Photo;
import com.aork.base.unsplash.User;
import com.aork.common.ui.adapter.photo.PhotoAdapter;
import com.aork.common.ui.widget.AutoHideInkPageIndicator;
import com.aork.common.ui.widget.CircularImageView;
import com.aork.common.ui.widget.NestedScrollAppBarLayout;
import com.aork.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.aork.common.utils.DisplayUtils;
import com.aork.common.utils.ShareUtils;
import com.aork.common.utils.helper.NotificationHelper;
import com.aork.common.utils.manager.AuthManager;
import com.aork.common.ui.adapter.PagerAdapter;
import com.aork.common.utils.AnimUtils;
import com.aork.common.utils.BackToTopUtils;
import com.aork.common.utils.manager.ThemeManager;
import com.aork.common.presenter.pager.PagerViewManagePresenter;
import com.aork.user.di.component.DaggerApplicationComponent;
import com.aork.user.ui.UserCollectionsView;
import com.aork.user.ui.UserPhotosView;
import com.aork.user.ui.UserProfileView;
import com.aork.user.vm.UserActivityModel;
import com.aork.user.vm.UserCollectionsViewModel;
import com.aork.user.vm.UserLikesViewModel;
import com.aork.user.vm.UserPhotosViewModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User activity.
 *
 * This activity is used to show the information of a user.
 *
 * */

@Route(path = UserActivity.USER_ACTIVITY)
public class UserActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener, ViewPager.OnPageChangeListener,
        SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_user_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_user_container) CoordinatorLayout container;
    @BindView(R2.id.activity_user_shadow) View shadow;

    @BindView(R2.id.activity_user_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_user_toolbar) Toolbar toolbar;
    @BindView(R2.id.activity_user_avatar) CircularImageView avatar;
    @BindView(R2.id.activity_user_title) TextView title;
    @OnClick(R2.id.activity_user_title) void clickTitle() {
        if (AuthManager.getInstance().isAuthorized()) {
            User user = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);
            if (user != null) {
                ProfileDialog dialog = new ProfileDialog();
                dialog.setUsername(user.username);
                dialog.show(getSupportFragmentManager(), null);
            }
        }
    }
    @BindView(R2.id.activity_user_profileView) UserProfileView userProfileView;

    @BindView(R2.id.activity_user_viewPager) FitBottomSystemBarViewPager viewPager;
    @BindView(R2.id.activity_user_indicator) AutoHideInkPageIndicator indicator;
    private PagerAdapter adapter;

    private PagerView[] pagers = new PagerView[pageCount()];
    private BaseAdapter[] adapters = new BaseAdapter[pageCount()];

    private UserActivityModel activityModel;
    private PagerManageViewModel pagerManageModel;
    private PagerViewModel[] pagerModels = new PagerViewModel[pageCount()];
    private UserPhotosViewModel photoPagerModel;
    private UserLikesViewModel likesPagerModel;
    private UserCollectionsViewModel collectionsPagerModel;
    @Inject ParamsViewModelFactory viewModelFactory;

    private BrowsableDialogMangePresenter browsableDialogMangePresenter;
    private Handler handler;

    public static final String USER_ACTIVITY = "/user/UserActivity";
    public static final String KEY_USER_ACTIVITY_USER = "user_activity_user";
    public static final String KEY_USER_ACTIVITY_USERNAME = "user_activity_username";
    public static final String KEY_USER_ACTIVITY_PAGE_POSITION = "user_activity_page_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        initModel();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void handleBackPressed() {
        if (pagers[getCurrentPagerPosition()].checkNeedBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            if (isTheLowestLevel()) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
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
        finish();
        if (backPressed) {
            // overridePendingTransition(R.anim.none, R.anim.activity_slide_out);
        } else {
            overridePendingTransition(R.anim.none, R.anim.activity_fade_out);
        }
    }

    @androidx.annotation.Nullable
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
        if (getCurrentPagerPosition() != ProfilePager.PAGE_COLLECTION) {
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
        User user = getIntent().getParcelableExtra(KEY_USER_ACTIVITY_USER);
        String username = getIntent().getStringExtra(KEY_USER_ACTIVITY_USERNAME);
        int page = getIntent().getIntExtra(KEY_USER_ACTIVITY_PAGE_POSITION, ProfilePager.PAGE_PHOTO);

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(UserActivityModel.class);
        if (user != null) {
            activityModel.init(Resource.success(user), user.username);
        } else if (!TextUtils.isEmpty(username)) {
            activityModel.init(Resource.error(null), username);
        } else {
            activityModel.init(Resource.error(null), "unsplash");
        }

        pagerManageModel = ViewModelProviders.of(this, viewModelFactory).get(PagerManageViewModel.class);
        pagerManageModel.init(page);

        photoPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(UserPhotosViewModel.class);
        photoPagerModel.init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                activityModel.getUsername()
        );
        pagerModels[ProfilePager.PAGE_PHOTO] = photoPagerModel;

        likesPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(UserLikesViewModel.class);
        likesPagerModel.init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                activityModel.getUsername()
        );
        pagerModels[ProfilePager.PAGE_LIKE] = likesPagerModel;

        collectionsPagerModel = ViewModelProviders.of(this, viewModelFactory)
                .get(UserCollectionsViewModel.class);
        collectionsPagerModel.init(
                ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                activityModel.getUsername()
        );
        pagerModels[ProfilePager.PAGE_COLLECTION] = collectionsPagerModel;
    }

    private void initView() {
        swipeBackView.setOnSwipeListener(this);

        if (isTheLowestLevel()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_home_light, R.drawable.ic_toolbar_home_dark);
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        }
        DisplayUtils.inflateToolbarMenu(toolbar, R.menu.activity_user_toolbar, this);
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });

        initPages();

        browsableDialogMangePresenter = new BrowsableDialogMangePresenter() {
            @Override
            public void finishActivity() {
                finishSelf(true);
            }
        };

        userProfileView.setOnRippleButtonSwitchedListener(switchTo ->
                activityModel.followOrCancelFollowUser(switchTo));
        userProfileView.setAdapter(adapter);

        handler = new Handler();
        handler.postDelayed(() ->
                activityModel.getResource().observe(
                        this, userResource -> drawProfile(userResource.data)
                ), 1000
        );
        activityModel.getResource().observe(this, resource -> {
            if (resource.data == null) {
                if (resource.status == Resource.Status.LOADING) {
                    browsableDialogMangePresenter.load(this);
                } else {
                    browsableDialogMangePresenter.error(this, () -> activityModel.requestUser());
                }
                return;
            }

            browsableDialogMangePresenter.success();

            if (TextUtils.isEmpty(photoPagerModel.getUsername())) {
                photoPagerModel.setUsername(resource.data.username);
                photoPagerModel.refresh();
            }
            if (TextUtils.isEmpty(likesPagerModel.getUsername())) {
                likesPagerModel.setUsername(resource.data.username);
                likesPagerModel.refresh();
            }
            if (TextUtils.isEmpty(collectionsPagerModel.getUsername())) {
                collectionsPagerModel.setUsername(resource.data.username);
                collectionsPagerModel.refresh();
            }

            if (TextUtils.isEmpty(resource.data.portfolio_url)) {
                toolbar.getMenu().getItem(0).setVisible(false);
            } else {
                toolbar.getMenu().getItem(0).setVisible(true);
            }

            LoadImagePresenter.loadUserAvatar(this, avatar, resource.data, null);

            title.setText(resource.data.name);
        });
    }

    @SuppressWarnings("unchecked")
    private void initPages() {
        photoPagerModel.readDataList(list ->
                adapters[ProfilePager.PAGE_PHOTO] = new PhotoAdapter(this, list).setItemEventCallback(
                        new PhotoItemEventHelper(
                                this, photoPagerModel, (context, photo) -> downloadPhoto(photo)
                        )
                )
        );

        likesPagerModel.readDataList(list ->
                adapters[ProfilePager.PAGE_LIKE] = new PhotoAdapter(this, list).setItemEventCallback(
                        new PhotoItemEventHelper(
                                this, likesPagerModel, (context, photo) -> downloadPhoto(photo)
                        )
                )
        );

        collectionsPagerModel.readDataList(list ->
                adapters[ProfilePager.PAGE_COLLECTION] = new CollectionAdapter(this, list).setItemEventCallback(
                        new CollectionItemEventHelper(this)
                )
        );

        List<View> pageList = new ArrayList<>(
                Arrays.asList(
                        new UserPhotosView(
                                this,
                                (PhotoAdapter) adapters[ProfilePager.PAGE_PHOTO],
                                ProfilePager.PAGE_PHOTO,
                                this
                        ), new UserPhotosView(
                                this,
                                (PhotoAdapter) adapters[ProfilePager.PAGE_LIKE],
                                ProfilePager.PAGE_LIKE,
                                this
                        ), new UserCollectionsView(
                                this,
                                (CollectionAdapter) adapters[ProfilePager.PAGE_COLLECTION],
                                ProfilePager.PAGE_COLLECTION,
                                this
                        )
                )
        );
        for (int i = 0; i < pageList.size(); i ++) {
            pagers[i] = (PagerView) pageList.get(i);
        }

        String[] userTabs = getResources().getStringArray(R.array.user_tabs);

        List<String> tabList = new ArrayList<>();
        Collections.addAll(tabList, userTabs);
        this.adapter = new PagerAdapter(viewPager, pageList, tabList);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(getCurrentPagerPosition(), false);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = findViewById(R.id.activity_user_tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayoutDoubleClickBackToTopPresenter(UserActivity.this::backToTop));

        indicator.setViewPager(viewPager);
        indicator.setAlpha(0f);

        pagerManageModel.getPagerPosition().observe(this, position -> {
            for (int i = ProfilePager.PAGE_PHOTO; i < pageCount(); i ++) {
                pagers[i].setSelected(i == position);
            }

            if (pagerModels[getCurrentPagerPosition()].getListSize() == 0
                    && pagerModels[getCurrentPagerPosition()].getListState() != ListResource.State.REFRESHING
                    && pagerModels[getCurrentPagerPosition()].getListState() != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(
                        (PagerViewModel<?>) pagerModels[getCurrentPagerPosition()],
                        adapters[getCurrentPagerPosition()]
                );
            }
        });

        for (int i = ProfilePager.PAGE_PHOTO; i < pageCount(); i ++) {
            int finalI = i;
            pagerModels[i].observeListResource(this, viewModel ->
                    PagerViewManagePresenter.responsePagerListResourceChanged(
                            viewModel, pagers[finalI], adapters[finalI]
                    )
            );
        }

        AnimUtils.translationYInitShow(viewPager, 400);
    }

    private void drawProfile(@Nullable User user) {
        if (user == null) {
            return;
        }
        if (user.isComplete() && userProfileView.getState() == UserProfileView.STATE_LOADING) {
            TransitionManager.beginDelayedTransition(container);
            userProfileView.drawUserInfo(this, user);
        } else if (userProfileView.getState() == UserProfileView.STATE_NORMAL) {
            userProfileView.setRippleButtonState(user);
        }
    }

    // control.

    private int getCurrentPagerPosition() {
        if (pagerManageModel.getPagerPosition().getValue() == null) {
            return ProfilePager.PAGE_PHOTO;
        } else {
            return pagerManageModel.getPagerPosition().getValue();
        }
    }

    private static int pageCount() {
        return 3;
    }

    public void downloadPhoto(Photo photo) {
        requestReadWritePermission(photo, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                ComponentFactory.getDownloaderService().addTask(
                        UserActivity.this,
                        (Photo) downloadable,
                        DownloadTask.DOWNLOAD_TYPE,
                        ComponentFactory.getSettingsService().getDownloadScale()
                );
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        UserActivity.this, getString(R.string.feedback_need_permission));
            }
        });
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
        int i = item.getItemId();
        if (i == R.id.action_open_portfolio) {
            if (activityModel.getResource().getValue() != null
                    && activityModel.getResource().getValue().data != null) {
                String url = activityModel.getResource().getValue().data.portfolio_url;
                if (!TextUtils.isEmpty(url)) {
                    RoutingHelper.startWebActivity(this, url);
                } else {
                    NotificationHelper.showSnackbar(getString(R.string.feedback_portfolio_is_null));
                }
            }
        } else if (i == R.id.action_share) {
            if (activityModel.getResource().getValue() != null
                    && activityModel.getResource().getValue().data != null) {
                ShareUtils.shareUser(activityModel.getResource().getValue().data);
            }
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
        pagerManageModel.setPagerPosition(position);
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

    // on swipe listener. (swipe back listener)

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return pagers[getCurrentPagerPosition()].canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight()
                    + getResources().getDimensionPixelSize(R.dimen.tab_layout_height);
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
