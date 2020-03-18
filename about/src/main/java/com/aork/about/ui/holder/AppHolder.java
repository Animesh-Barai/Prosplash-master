package com.aork.about.ui.holder;

import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;
import android.widget.TextView;

import com.aork.about.R2;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.about.model.AboutModel;
import com.aork.about.ui.AboutAdapter;
import com.aork.about.model.AppObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * App holder.
 *
 * This ViewHolder class is used to show app information for {@link AboutAdapter}.
 *
 * */

public class AppHolder extends AboutAdapter.ViewHolder {

    @BindView(R2.id.item_about_app_icon) AppCompatImageView icon;
    @BindView(R2.id.item_about_app_title) TextView text;

    private int id;

    public AppHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindView(MysplashActivity a, AboutModel model) {
        AppObject object = (AppObject) model;

        icon.setImageResource(object.iconId);
        text.setText(object.text);
        id = object.id;
    }

    @Override
    protected void onRecycled() {
        // do nothing.
    }

   /* @OnClick(R2.id.item_about_app_container) void clickItem() {
        MysplashActivity activity = MysplashApplication.getInstance().getTopActivity();
        if (activity == null) {
            return;
        }

        switch (id) {
            case 1:
                ComponentFactory.getAboutModule().watchAllIntroduce(activity);
                break;

           case 2:
                RoutingHelper.startWebActivity(activity, "https://github.com/WangDaYeeeeee");
                break;

            case 3:
                RoutingHelper.startWebActivity(activity, "mailto:animeshbarai14@gmail.com");
                break;

            case 4:
                RoutingHelper.startWebActivity(activity, "https://github.com/WangDaYeeeeee/MySplash");
                break;

         /*   case 5:
                if (AlipayDonate.hasInstalledAlipayClient(activity)) {
                    AlipayDonate.startAlipayClient(activity, "fkx02882gqdh6imokjddj2a");
                } else {
                    NotificationHelper.showSnackbar(activity, "Alipay is not installed.");
                }
                break;

            case 6:
                if (WeiXinDonate.hasInstalledWeiXinClient(activity)) {
                    new WechatDonateDialog().show(activity.getSupportFragmentManager(), null);
                } else {
                    NotificationHelper.showSnackbar(activity, "WeChat is not installed.");
                }
                break;
        }*/
    }

