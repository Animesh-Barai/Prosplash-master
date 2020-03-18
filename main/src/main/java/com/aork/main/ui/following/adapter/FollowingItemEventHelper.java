package com.aork.main.ui.following.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.aork.base.pager.ProfilePager;
import com.aork.common.presenter.LikePhotoPresenter;
import com.aork.common.utils.helper.RoutingHelper;
import com.aork.common.ui.dialog.DownloadRepeatDialog;
import com.aork.common.ui.dialog.SelectCollectionDialog;
import com.aork.component.ComponentFactory;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.common.utils.helper.NotificationHelper;
import com.aork.base.unsplash.Photo;
import com.aork.base.unsplash.User;
import com.aork.common.utils.FileUtils;
import com.aork.common.utils.manager.AuthManager;
import com.aork.common.presenter.DispatchCollectionsChangedPresenter;
import com.aork.main.R;
import com.aork.main.vm.FollowingHomePageViewModel;

public class FollowingItemEventHelper implements FollowingAdapter.ItemEventCallback {

    private MysplashActivity activity;
    private FollowingHomePageViewModel viewModel;
    private DownloadExecutor executor;

    public FollowingItemEventHelper(MysplashActivity activity, FollowingHomePageViewModel viewModel,
                                    DownloadExecutor executor) {
        this.activity = activity;
        this.viewModel = viewModel;
        this.executor = executor;
    }

    @Override
    public void onStartPhotoActivity(View image, View background,
                                     int adapterPosition, int photoPosition) {
        viewModel.readDataList(list ->
            ComponentFactory.getPhotoModule().startPhotoActivity(
                    activity, image, background, list.get(photoPosition), photoPosition)
        );
    }

    @Override
    public void onStartUserActivity(View avatar, View background, User user,
                                    @ProfilePager.ProfilePagerRule int index) {
        ComponentFactory.getUserModule().startUserActivity(
                activity, avatar, background, user, ProfilePager.PAGE_PHOTO);
    }

    @Override
    public void onVerbClicked(String verb, int adapterPosition) {
        if (!TextUtils.isEmpty(verb)) {
            ComponentFactory.getSearchModule().startSearchActivity(activity, verb);
        }
    }

    @Override
    public void onLikeButtonClicked(Photo photo, int adapterPosition, boolean setToLike) {
        if (AuthManager.getInstance().isAuthorized()) {
            if (setToLike) {
                LikePhotoPresenter.getInstance().like(photo);
            } else {
                LikePhotoPresenter.getInstance().unlike(photo);
            }
        } else {
            ComponentFactory.getMeModule().startLoginActivity(activity);
        }
    }

    @Override
    public void onCollectButtonClicked(Photo photo, int adapterPosition) {
        if (!AuthManager.getInstance().isAuthorized()) {
            ComponentFactory.getMeModule().startLoginActivity(activity);
        } else {
            SelectCollectionDialog dialog = new SelectCollectionDialog();
            dialog.setPhotoAndListener(photo, new DispatchCollectionsChangedPresenter());
            dialog.show(activity.getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onDownloadButtonClicked(Photo photo, int adapterPosition) {
        if (ComponentFactory.getDownloaderService().isDownloading(activity, photo.id)) {
            NotificationHelper.showSnackbar(activity, activity.getString(R.string.feedback_download_repeat));
        } else if (FileUtils.isPhotoExists(activity, photo.id)) {
            DownloadRepeatDialog dialog = new DownloadRepeatDialog();
            dialog.setDownloadKey(photo);
            dialog.setOnCheckOrDownloadListener(new DownloadRepeatDialog.OnCheckOrDownloadListener() {
                @Override
                public void onCheck(Object obj) {
                    RoutingHelper.startCheckPhotoActivity(activity, photo.id);
                }

                @Override
                public void onDownload(Object obj) {
                    executor.execute(activity, photo);
                }
            });
            dialog.show(activity.getSupportFragmentManager(), null);
        } else {
            executor.execute(activity, photo);
        }
    }

    public interface DownloadExecutor {
        void execute(Context context, Photo photo);
    }
}
