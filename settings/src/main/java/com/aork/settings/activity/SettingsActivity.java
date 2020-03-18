package com.aork.settings.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.settings.R;
import com.aork.settings.R2;
import com.aork.settings.fragment.SettingsFragment;
import com.aork.common.ui.widget.swipeBackView.SwipeBackCoordinatorLayout;
import com.aork.common.utils.helper.NotificationHelper;
import com.aork.common.utils.manager.ThemeManager;

import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Settings activity.
 *
 * This activity is used to show and save setting options.
 *
 * */

@Route(path = SettingsActivity.SETTINGS_ACTIVITY)
public class SettingsActivity extends MysplashActivity
        implements SwipeBackCoordinatorLayout.OnSwipeListener {

    @BindView(R2.id.activity_settings_swipeBackView) SwipeBackCoordinatorLayout swipeBackView;
    @BindView(R2.id.activity_settings_container) CoordinatorLayout container;

    public static final String SETTINGS_ACTIVITY = "/settings/SettingsActivity";

    public static final int ACTIVITY_REQUEST_CODE_CUSTOM_API = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initWidget();
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_settings_preferenceContainer, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_REQUEST_CODE_CUSTOM_API:
                if (resultCode == RESULT_OK) {
                    NotificationHelper.showSnackbar(
                            this, getString(R.string.feedback_please_login));
                }
                break;
        }
    }

    @Override
    public void handleBackPressed() {
        finishSelf(true);
    }

    @Override
    protected void backToTop() {
        // do nothing.
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

    @Nullable
    @Override
    protected SwipeBackCoordinatorLayout provideSwipeBackView() {
        return swipeBackView;
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initWidget() {
        swipeBackView.setOnSwipeListener(this);

        Toolbar toolbar = findViewById(R.id.activity_settings_toolbar);
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_back_light, R.drawable.ic_toolbar_back_dark);
        toolbar.setNavigationOnClickListener(v -> finishSelf(true));
    }

    // interface.

    // on swipe back listener.

    @Override
    public boolean canSwipeBack(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        return true;
    }

    @Override
    public void onSwipeProcess(@SwipeBackCoordinatorLayout.DirectionRule int dir, float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(@SwipeBackCoordinatorLayout.DirectionRule int dir) {
        finishSelf(false);
    }
}
