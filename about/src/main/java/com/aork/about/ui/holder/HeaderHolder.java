package com.aork.about.ui.holder;

import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;
import android.widget.TextView;

import com.aork.about.R;
import com.aork.about.R2;
import com.aork.about.model.AboutModel;
import com.aork.about.ui.AboutAdapter;
import com.aork.about.ui.TotalDialog;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.common.base.application.MysplashApplication;
import com.aork.common.image.ImageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Header holder.
 *
 * This ViewHolder class is used to show header for {@link AboutAdapter}.
 *
 * */

public class HeaderHolder extends AboutAdapter.ViewHolder {

    @BindView(R2.id.item_about_header_appIcon) AppCompatImageView appIcon;

    @OnClick(R2.id.item_about_header_backButton) void close() {
        MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
        if (activity != null) {
            activity.finishSelf(true);
        }
    }

    @OnClick(R2.id.item_about_header_unsplashContainer) void checkTotal() {
        MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
        if (activity != null) {
            TotalDialog dialog = new TotalDialog();
            dialog.show(activity.getSupportFragmentManager(), null);
        }
    }

    public HeaderHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        TextView unsplashTitle = itemView.findViewById(R.id.item_about_header_unsplashTitle);
        unsplashTitle.setText(itemView.getContext().getString(R.string.unsplash));


        TextView unsplashContent = itemView.findViewById(R.id.item_about_header_unsplashContent);
        unsplashContent.setText(itemView.getContext().getString(R.string.about_unsplash));
    }

    @Override
    protected void onBindView(MysplashActivity a, AboutModel model) {
        ImageHelper.loadImage(a, appIcon, R.drawable.ic_launcher);
    }

    @Override
    protected void onRecycled() {
        ImageHelper.releaseImageView(appIcon);
    }
}
