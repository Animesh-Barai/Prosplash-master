package com.aork.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.aork.common.R;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.common.utils.helper.NotificationHelper;
import com.aork.component.service.SettingsService;
import com.aork.settings.base.RoutingHelper;

/**
 * Settings option manager.
 *
 * A manager that is used to manage setting options.
 *
 * */

public class SettingsServiceIMP implements SettingsService {

    private volatile static SettingsServiceIMP instance;

    public static SettingsServiceIMP getInstance(Context context) {
        if (instance == null) {
            synchronized (SettingsServiceIMP.class) {
                if (instance == null) {
                    instance = new SettingsServiceIMP(context);
                }
            }
        }
        return instance;
    }

    private String backToTopType;
    private boolean notifiedSetBackToTop;
    private String autoNightMode;
    private boolean cdnEnabled;
    private String language;

    private String downloader;
    private String downloadScale;

    private boolean saturationAnimationEnabled;
    private String saturationAnimationDuration;
    private boolean showGridInPort;
    private boolean showGridInLand;

    private SettingsServiceIMP(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        backToTopType = sharedPreferences.getString(
                context.getString(R.string.key_back_to_top), BACK_TO_TOP_TYPE_ALL);

        notifiedSetBackToTop = sharedPreferences.getBoolean(
                context.getString(R.string.key_notified_set_back_to_top), false);

        autoNightMode = sharedPreferences.getString(
                context.getString(R.string.key_auto_night_mode), DAY_NIGHT_MODE_FOLLOW_SYSTEM);

        cdnEnabled = sharedPreferences.getBoolean(
                context.getString(R.string.key_cdn_enabled), false);

        language = sharedPreferences.getString(
                context.getString(R.string.key_language), LANGUAGE_SYSTEM);

        downloader = sharedPreferences.getString(
                context.getString(R.string.key_downloader), DOWNLOADER_MYSPLASH);

        downloadScale = sharedPreferences.getString(
                context.getString(R.string.key_download_scale), DOWNLOAD_SCALE_COMPACT);

        saturationAnimationEnabled = sharedPreferences.getBoolean(
                context.getString(R.string.key_saturation_animation_enabled), true);

        saturationAnimationDuration = sharedPreferences.getString(
                context.getString(R.string.key_saturation_animation_duration),
                SATURATION_ANIM_DURATION_NORMAL
        );

        showGridInPort = sharedPreferences.getBoolean(
                context.getString(R.string.key_grid_list_in_port), true);

        showGridInLand = sharedPreferences.getBoolean(
                context.getString(R.string.key_grid_list_in_land), true);
    }

    @Override
    public String getBackToTopType() {
        return backToTopType;
    }

    public void setBackToTopType(String backToTopType) {
        this.backToTopType = backToTopType;
    }

    @Override
    public boolean notifySetBackToTop(Activity activity) {
        if (notifiedSetBackToTop) {
            return false;
        }

        notifiedSetBackToTop = true;
        PreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putBoolean(
                        activity.getString(R.string.key_notified_set_back_to_top),
                        notifiedSetBackToTop
                ).apply();
        if (activity instanceof MysplashActivity) {
            NotificationHelper.showActionSnackbar(
                    (MysplashActivity) activity,
                    activity.getString(R.string.feedback_notify_set_back_to_top),
                    activity.getString(R.string.set),
                    v -> startSettingsActivity(activity)
            );
        }
        return true;
    }

    @Override
    public String getAutoNightMode() {
        return autoNightMode;
    }

    public void setAutoNightMode(String autoNightMode) {
        this.autoNightMode = autoNightMode;
    }

    @Override
    public boolean isSaturationAnimationEnabled() {
        return saturationAnimationEnabled;
    }

    public void setSaturationAnimationEnabled(boolean saturationAnimationEnabled) {
        this.saturationAnimationEnabled = saturationAnimationEnabled;
    }

    @Override
    public String getSaturationAnimationDuration() {
        return saturationAnimationDuration;
    }

    public void setSaturationAnimationDuration(String duration) {
        this.saturationAnimationDuration = duration;
    }

    @Override
    public boolean isCDNEnabled() {
        return cdnEnabled;
    }

    public void setCDNEnabled(boolean enabled) {
        this.cdnEnabled = enabled;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getDownloader() {
        return downloader;
    }

    public void setDownloader(String downloader) {
        this.downloader = downloader;
    }

    @Override
    public String getDownloadScale() {
        return downloadScale;
    }

    public void setDownloadScale(String downloadScale) {
        this.downloadScale = downloadScale;
    }

    @Override
    public boolean isShowGridInPort() {
        return showGridInPort;
    }

    @Override
    public boolean isShowGridInLand() {
        return showGridInLand;
    }

    @Override
    public void startSettingsActivity(Activity a) {
        RoutingHelper.startSettingsActivity(a);
    }
}
