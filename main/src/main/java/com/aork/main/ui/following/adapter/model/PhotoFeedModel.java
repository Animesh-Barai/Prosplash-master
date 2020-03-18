package com.aork.main.ui.following.adapter.model;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.aork.base.unsplash.Photo;
import com.aork.common.base.adapter.BaseAdapter;
import com.aork.common.image.ImageHelper;
import com.aork.common.presenter.LikePhotoPresenter;
import com.aork.component.ComponentFactory;

public class PhotoFeedModel extends FollowingModel {

    public @NonNull String photoUrl;
    public @Nullable String thumbUrl;
    public @Size int[] photoSize;
    public @ColorInt int photoColor;

    public @Nullable String authorAvatarUrl;
    public @Size int[] authorAvatarSize;

    public String authorName;

    public boolean downloading;
    public boolean collected;
    public boolean likeProgressing;
    public boolean liked;
    public boolean hasFadeIn;

    public PhotoFeedModel(Context context, Photo photo, int adapterPosition, int photoPosition) {
        super(photo, adapterPosition, photoPosition);

        photoUrl = photo.getRegularUrl(context);
        thumbUrl = photo.urls.thumb;
        photoSize = photo.getRegularSize();
        photoColor = ImageHelper.computeCardBackgroundColor(context, photo.color);

        if (TextUtils.isEmpty(photo.user.profile_image.large)) {
            authorAvatarUrl = null;
        } else {
            authorAvatarUrl = photo.user.profile_image.large;
        }
        authorAvatarSize = new int[] {ImageHelper.AVATAR_SIZE, ImageHelper.AVATAR_SIZE};

        authorName = photo.user.name;

        this.downloading = ComponentFactory.getDownloaderService().isDownloading(context, photo.id);
        this.collected = photo.current_user_collections != null && photo.current_user_collections.size() != 0;
        this.likeProgressing = LikePhotoPresenter.getInstance().isInProgress(photo);
        this.liked = photo.liked_by_user;
        this.hasFadeIn = false;
    }

    @Override
    public boolean areItemsTheSame(BaseAdapter.ViewModel newModel) {
        return newModel instanceof PhotoFeedModel && ((PhotoFeedModel) newModel).photo.id.equals(photo.id);
    }

    @Override
    public boolean areContentsTheSame(BaseAdapter.ViewModel newModel) {
        ((PhotoFeedModel) newModel).hasFadeIn = hasFadeIn;

        return ImageHelper.isSameUrl(((PhotoFeedModel) newModel).photoUrl, photoUrl)
                && ImageHelper.isSameUrl(((PhotoFeedModel) newModel).authorAvatarUrl, authorAvatarUrl)
                && ((PhotoFeedModel) newModel).authorName.equals(authorName)
                && ((PhotoFeedModel) newModel).downloading == downloading
                && ((PhotoFeedModel) newModel).collected == collected
                && ((PhotoFeedModel) newModel).likeProgressing == likeProgressing
                && ((PhotoFeedModel) newModel).liked == liked;
    }

    @Override
    public Object getChangePayload(BaseAdapter.ViewModel newModel) {
        if (ImageHelper.isSameUrl(((PhotoFeedModel) newModel).photoUrl, photoUrl)
                && ImageHelper.isSameUrl(((PhotoFeedModel) newModel).authorAvatarUrl, authorAvatarUrl)) {
            return 1;
        }
        return null;
    }
}
