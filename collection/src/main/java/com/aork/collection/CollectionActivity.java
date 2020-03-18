package com.aork.collection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.AppCompatImageView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.aork.base.i.Downloadable;
import com.aork.collection.di.component.DaggerApplicationComponent;
import com.aork.collection.ui.CollectionPhotosView;
import com.aork.collection.ui.UpdateCollectionDialog;
import com.aork.collection.vm.CollectionActivityModel;
import com.aork.collection.vm.CollectionPhotosViewModel;
import com.aork.common.base.activity.LoadableActivity;
import com.aork.base.DownloadTask;
import com.aork.base.i.PagerManageView;
import com.aork.base.pager.ListPager;
import com.aork.base.pager.ProfilePager;
import com.aork.base.resource.ListResource;
import com.aork.base.resource.Resource;
import com.aork.base.unsplash.Collection;
import com.aork.base.unsplash.Photo;
import com.aork.base.unsplash.User;
import com.aork.common.bus.MessageBus;
import com.aork.common.bus.event.CollectionEvent;
import com.aork.common.base.vm.ParamsViewModelFactory;
import com.aork.common.image.transformation.CircleTransformation;
import com.aork.common.image.ImageHelper;
import com.aork.common.presenter.BrowsableDialogMangePresenter;
import com.aork.common.presenter.LoadImagePresenter;
import com.aork.common.presenter.pager.PagerLoadablePresenter;
import com.aork.common.presenter.pager.PagerViewManagePresenter;
import com.aork.common.utils.helper.RoutingHelper;
import com.aork.common.ui.adapter.photo.PhotoAdapter;
import com.aork.common.ui.adapter.tag.MiniTagAdapter;
import com.aork.common.ui.dialog.DownloadRepeatDialog;
import com.aork.common.ui.widget.CircularImageView;
import com.aork.common.ui.widget.NestedScrollAppBarLayout;
import com.aork.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.aork.common.utils.AnimUtils;
import com.aork.common.utils.BackToTopUtils;
import com.aork.common.utils.DisplayUtils;
import com.aork.common.utils.ShareUtils;
import com.aork.common.utils.helper.NotificationHelper;
import com.aork.common.utils.manager.AuthManager;
import com.aork.common.utils.manager.ThemeManager;
import com.aork.component.ComponentFactory;
import com.aork.collection.base.CollectionPhotoItemEventHelper;
import com.aork.component.service.MuzeiService;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Collection activity.
 *
 * This activity is used to show a collection.
 *
 * */

@Route(path = CollectionActivity.COLLECTION_ACTIVITY)
public class CollectionActivity extends LoadableActivity<Photo>
        implements PagerManageView, Toolbar.OnMenuItemClickListener, SwipeBackCoordinatorLayout.OnSwipeListener,
        UpdateCollectionDialog.OnCollectionChangedListener, DownloadRepeatDialog.OnCheckOrDownloadListener {

    @BindView(R2.id.activity_collection_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_collection_container) CoordinatorLayout container;
    @BindView(R2.id.activity_collection_shadow) View shadow;

    @BindView(R2.id.activity_collection_appBar) NestedScrollAppBarLayout appBar;
    @BindView(R2.id.activity_collection_coverImage) AppCompatImageView cover;
    @BindView(R2.id.activity_collection_toolbar) Toolbar toolbar;
    @BindView(R2.id.activity_collection_title) TextView title;
    @BindView(R2.id.activity_collection_tagList) RecyclerView tagList;
    @BindView(R2.id.activity_collection_description) TextView description;
    @BindView(R2.id.activity_collection_avatar) CircularImageView avatar;
    @BindView(R2.id.activity_collection_subtitle) TextView subtitle;

    @OnClick(R2.id.activity_collection_touchBar) void checkAuthor() {
        try {
            ComponentFactory.getUserModule().startUserActivity(
                    this, avatar, appBar,
                    Objects.requireNonNull(getCollection()).user, ProfilePager.PAGE_PHOTO
            );
        } catch (Exception ignore) {
            // do nothing.
        }
    }

    @BindView(R2.id.activity_collection_photosView) CollectionPhotosView photosView;
    PhotoAdapter photoAdapter;

    private CollectionActivityModel activityModel;
    private CollectionPhotosViewModel photosViewModel;
    @Inject ParamsViewModelFactory viewModelFactory;
    
    private BrowsableDialogMangePresenter browsableDialogMangePresenter;
    
    public static final String COLLECTION_ACTIVITY = "/collection/CollectionActivity";
    public static final String KEY_COLLECTION_ACTIVITY_COLLECTION = "collection_activity_collection";
    public static final String KEY_COLLECTION_ACTIVITY_ID = "collection_activity_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DaggerApplicationComponent.create().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        initModel();
        initView();
    }

    @Override
    public void handleBackPressed() {
        if (photosView.checkNeedBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishSelf(true);
        }
    }

    @Override
    public void backToTop() {
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.scrollToPageTop();
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

    @Override
    public List<Photo> loadMoreData(int currentCount) {
        return PagerLoadablePresenter.loadMore(photosViewModel, currentCount,
                photosView, photosView.getRecyclerView(), this, 0);
    }

    @Override
    public boolean isValidProvider(Class clazz) {
        return clazz == Photo.class;
    }

    // init.

    private void initModel() {
        Collection collection = getIntent().getParcelableExtra(KEY_COLLECTION_ACTIVITY_COLLECTION);
        String collectionId = getIntent().getStringExtra(KEY_COLLECTION_ACTIVITY_ID);

        activityModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionActivityModel.class);
        photosViewModel = ViewModelProviders.of(this, viewModelFactory).get(CollectionPhotosViewModel.class);
        if (collection != null) {
            activityModel.init(Resource.success(collection), collection.id, collection.curated);
            photosViewModel.init(
                    ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                    collection.id,
                    collection.curated,
                    editVisible()
            );
        } else if (!TextUtils.isEmpty(collectionId)) {
            activityModel.init(
                    Resource.loading(null),
                    Integer.parseInt(collectionId),
                    Integer.parseInt(collectionId) < 1000
            );
            photosViewModel.init(
                    ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                    Integer.parseInt(collectionId),
                    Integer.parseInt(collectionId) < 1000,
                    editVisible()
            );
        } else {
            activityModel.init(Resource.loading(null), 1, true);
            photosViewModel.init(
                    ListResource.refreshing(0, ListPager.DEFAULT_PER_PAGE),
                    CollectionPhotosViewModel.INVALID_COLLECTION_ID,
                    false,
                    editVisible()
            );
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        swipeBackView.setOnSwipeListener(this);

        if (isTheLowestLevel()) {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_home, R.drawable.ic_toolbar_home_dark
            );
        } else {
            ThemeManager.setNavigationIcon(
                    toolbar, R.drawable.ic_back, R.drawable.ic_back
            );
        }
        toolbar.setNavigationOnClickListener(v -> {
            if (isTheLowestLevel()) {
                ComponentFactory.getMainModule().startMainActivity(this);
            }
            finishSelf(true);
        });
        DisplayUtils.inflateToolbarMenu(toolbar, R.menu.activity_collection_toolbar, this);
        toolbar.getMenu().getItem(0).setVisible(editVisible());
        toolbar.getMenu().getItem(2).setVisible(setAsSourceVisible());
        toolbar.getMenu().getItem(3).setVisible(removeSourceVisible());

        photosViewModel.readDataList(list ->
                photoAdapter = new PhotoAdapter(this, list).setItemEventCallback(
                        new CollectionPhotoItemEventHelper(
                                this,
                                photosViewModel,
                                (context, photo) -> requestPermissionAndDownload(photo)
                        )
                )
        );
        photosView.setPhotoAdapter(photoAdapter);
        photosView.setPagerManageView(this);

        browsableDialogMangePresenter = new BrowsableDialogMangePresenter() {
            @Override
            public void finishActivity() {
                finishSelf(true);
            }
        };

        activityModel.getResource().observe(this, resource -> {
            Collection collection = resource.data;
            if (collection == null) {
                if (resource.status == Resource.Status.LOADING) {
                    browsableDialogMangePresenter.load(this);
                } else {
                    browsableDialogMangePresenter.error(this, () -> activityModel.requestACollection());
                }
                return;
            }

            browsableDialogMangePresenter.success();

            photosViewModel.setCollectionId(collection.id);
            photosViewModel.setCurated(collection.curated);

            LoadImagePresenter.loadCollectionCover(this, cover, collection, null);

            title.setText(collection.title);

            if (collection.tags == null || collection.tags.size() == 0) {
                tagList.setVisibility(View.GONE);
            } else {
                tagList.setLayoutManager(
                        new LinearLayoutManager(
                                this, LinearLayoutManager.HORIZONTAL, false)
                );
                tagList.setAdapter(new MiniTagAdapter(
                        this,
                        collection.tags,
                        (view, tag) -> ComponentFactory.getSearchModule().startSearchActivity(this, view, tag)
                ));
            }

            if (TextUtils.isEmpty(collection.description)) {
                description.setVisibility(View.GONE);
            } else {
                description.setText(collection.description);
            }

            ImageHelper.loadImage(this, avatar, collection.user.profile_image.large, R.drawable.default_avatar_round, 
                    new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE}, 
                    new BitmapTransformation[] {new CircleTransformation(this)}, null);

            subtitle.setText(getString(R.string.by) + " " + collection.user.name);

            boolean myCollection = !TextUtils.isEmpty(AuthManager.getInstance().getUsername())
                    && AuthManager.getInstance().getUsername().equals(collection.user.username);
            photoAdapter.setShowDeleteButton(myCollection);

            if (photosViewModel.getListSize() == 0
                    && photosViewModel.getListState() != ListResource.State.REFRESHING
                    && photosViewModel.getListState() != ListResource.State.LOADING) {
                PagerViewManagePresenter.initRefresh(photosViewModel, photoAdapter);
            }
        });
        activityModel.getDeleted().observe(this, deleted -> {
            if (deleted) {
                finishSelf(true);
            }
        });

        photosViewModel.observeListResource(this, viewModel ->
                PagerViewManagePresenter.responsePagerListResourceChanged(
                        viewModel, photosView, photoAdapter
                )
        );

        AnimUtils.translationYInitShow(photosView, 400);
    }

    private boolean editVisible() {
        Collection collection = getCollection();
        return collection == null
                || (AuthManager.getInstance().getUsername() != null
                && AuthManager.getInstance().getUsername().equals(collection.user.username));
    }

    private boolean setAsSourceVisible() {
        Collection collection = getCollection();
        MuzeiService muzeiService = ComponentFactory.getMuzeiService();

        return collection != null
                && muzeiService.isMuzeiInstalled(this)
                && muzeiService.getSource(this).equals(MuzeiService.SOURCE_COLLECTION)
                && ComponentFactory.getMuzeiService().getMuzeiWallpaperSource(this, collection) == null;
    }

    private boolean removeSourceVisible() {
        Collection collection = getCollection();
        MuzeiService muzeiService = ComponentFactory.getMuzeiService();

        return collection != null
                && muzeiService.isMuzeiInstalled(this)
                && muzeiService.getSource(this).equals(MuzeiService.SOURCE_COLLECTION)
                && ComponentFactory.getMuzeiService().getMuzeiWallpaperSource(this, collection) != null;
    }

    // download.

    /**
     * @param target {@link Photo} or {@link Collection}.
     * */
    public void requestPermissionAndDownload(@NonNull Object target) {
        requestReadWritePermission((Downloadable) target, new RequestPermissionCallback() {
            @Override
            public void onGranted(Downloadable downloadable) {
                Context context = CollectionActivity.this;
                if (downloadable instanceof Collection) {
                    ComponentFactory.getDownloaderService().addTask(context, (Collection) downloadable);
                } else if (downloadable instanceof Photo) {
                    ComponentFactory.getDownloaderService().addTask(
                            context,
                            (Photo) downloadable,
                            DownloadTask.DOWNLOAD_TYPE,
                            ComponentFactory.getSettingsService().getDownloadScale()
                    );
                }
            }

            @Override
            public void onDenied(Downloadable downloadable) {
                NotificationHelper.showSnackbar(
                        CollectionActivity.this, getString(R.string.feedback_need_permission));
            }
        });
    }

    // data.

    @Nullable
    public Collection getCollection() {
        return Objects.requireNonNull(activityModel.getResource().getValue()).data;
    }

    // interface.

    // pager manage view.

    @Override
    public void onRefresh(int index) {
        photosViewModel.refresh();
    }

    @Override
    public void onLoad(int index) {
        photosViewModel.load();
    }

    @Override
    public boolean canLoadMore(int index) {
        return photosViewModel.getListState() != ListResource.State.REFRESHING
                && photosViewModel.getListState() != ListResource.State.LOADING
                && photosViewModel.getListState() != ListResource.State.ALL_LOADED;
    }

    @Override
    public boolean isLoading(int index) {
        return photosViewModel.getListState() == ListResource.State.LOADING;
    }

    // on menu item click listener.

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_share) {
            if (getCollection() != null) {
                ShareUtils.shareCollection(getCollection());
            }
        } else if (i == R.id.action_edit) {
            UpdateCollectionDialog dialog = new UpdateCollectionDialog();
            dialog.setCollection(getCollection());
            dialog.setOnCollectionChangedListener(this);
            dialog.show(getSupportFragmentManager(), null);
        } else if (i == R.id.action_set_as_source) {
            if (getCollection() != null) {
                ComponentFactory.getMuzeiService().setAsMuzeiSource(this, getCollection());
            }
        } else if (i == R.id.action_remove_source) {
            if (getCollection() != null) {
                ComponentFactory.getMuzeiService().removeFromMuzeiSource(this, getCollection());
            }
        }
        return true;
    }

    // on swipe listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return photosView.canSwipeBack(dir)
                    && appBar.getY() <= -appBar.getMeasuredHeight();
        } else {
            return photosView.canSwipeBack(dir)
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

    // on collection changed listener.

    @Override
    public void onEditCollection(Collection c) {
        AuthManager.getInstance().getCollectionsManager().updateCollection(c);
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.UPDATE));
    }

    @Override
    public void onDeleteCollection(Collection c) {
        AuthManager.getInstance().getCollectionsManager().deleteCollection(c);
        MessageBus.getInstance().post(new CollectionEvent(c, CollectionEvent.Event.DELETE));

        User user = AuthManager.getInstance().getUser();
        if (user != null) {
            user.total_collections --;
            MessageBus.getInstance().post(user);
        }
    }

    // on check or downloadTarget listener. (Collection)

    @Override
    public void onCheck(Object obj) {
        RoutingHelper.startCheckCollectionActivity(this, String.valueOf(((Collection) obj).id));
    }

    @Override
    public void onDownload(Object obj) {
        requestPermissionAndDownload(obj);
    }
}