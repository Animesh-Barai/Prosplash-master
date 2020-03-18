package com.aork.common.ui.adapter.photo;

import android.content.Context;
import android.view.View;

import com.aork.common.R;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.base.pager.ProfilePager;
import com.aork.base.unsplash.Photo;
import com.aork.base.unsplash.User;
import com.aork.common.base.vm.pager.PagerViewModel;
import com.aork.common.presenter.DispatchCollectionsChangedPresenter;
import com.aork.common.presenter.LikePhotoPresenter;
import com.aork.common.ui.dialog.DownloadRepeatDialog;
import com.aork.common.ui.dialog.SelectCollectionDialog;
import com.aork.common.utils.FileUtils;
import com.aork.common.utils.helper.NotificationHelper;
import com.aork.common.utils.helper.RoutingHelper;
import com.aork.common.utils.manager.AuthManager;
import com.aork.component.ComponentFactory;

public class PhotoItemEventHelper implements PhotoAdapter.ItemEventCallback {

    private MysplashActivity activity;
    private PagerViewModel<Photo> viewModel;
    private DownloadExecutor executor;

    public PhotoItemEventHelper(MysplashActivity activity, PagerViewModel<Photo> viewModel,
                                DownloadExecutor executor) {
        this.activity = activity;
        this.viewModel = viewModel;
        this.executor = executor;
    }

    @Override
    public void onStartPhotoActivity(View image, View background, int adapterPosition) {
        viewModel.readDataList(list ->
                ComponentFactory.getPhotoModule().startPhotoActivity(
                        activity, image, background, list.get(adapterPosition), adapterPosition)
        );
    }

    @Override
    public void onStartUserActivity(View avatar, View background, User user, int index) {
        ComponentFactory.getUserModule()
                .startUserActivity(activity, avatar, background, user, ProfilePager.PAGE_PHOTO);
    }

    @Override
    public void onDeleteButtonClicked(Photo photo, int adapterPosition) {
        // do nothing.
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
            NotificationHelper.showSnackbar(
                    activity, activity.getString(R.string.feedback_download_repeat));
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
