package com.aork.user.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.alibaba.android.arouter.launcher.ARouter;
import com.aork.base.pager.ProfilePager;
import com.aork.base.unsplash.User;
import com.aork.common.ui.transition.sharedElement.RoundCornerTransition;
import com.aork.common.utils.manager.AuthManager;
import com.aork.component.ComponentFactory;
import com.aork.user.R;
import com.aork.user.UserActivity;

public class RoutingHelper {

    public static void startUserActivity(Activity a,
                                         View avatar, View background,
                                         User u, @ProfilePager.ProfilePagerRule int page) {
        if (AuthManager.getInstance().isAuthorized()
                && !TextUtils.isEmpty(AuthManager.getInstance().getUsername())
                && u.username.equals(AuthManager.getInstance().getUsername())) {
            ComponentFactory.getMeModule().startMeActivity(a, avatar, background, page);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle b = new Bundle();
            RoundCornerTransition.addExtraProperties(background, b);

            ARouter.getInstance()
                    .build(UserActivity.USER_ACTIVITY)
                    .withParcelable(UserActivity.KEY_USER_ACTIVITY_USER, u)
                    .withInt(UserActivity.KEY_USER_ACTIVITY_PAGE_POSITION, page)
                    .withBundle(a.getString(R.string.transition_user_background), b)
                    .withOptionsCompat(
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    a,
                                    Pair.create(avatar, a.getString(R.string.transition_user_avatar)),
                                    Pair.create(background, a.getString(R.string.transition_user_background))
                            )
                    ).navigation(a);
        } else {
            ARouter.getInstance()
                    .build(UserActivity.USER_ACTIVITY)
                    .withParcelable(UserActivity.KEY_USER_ACTIVITY_USER, u)
                    .withInt(UserActivity.KEY_USER_ACTIVITY_PAGE_POSITION, page)
                    // .withTransition(R.anim.activity_slide_in, R.anim.none)
                    .navigation(a);
        }
    }

    public static void startUserActivity(Activity a, String username) {
        ARouter.getInstance()
                .build(UserActivity.USER_ACTIVITY)
                .withString(UserActivity.KEY_USER_ACTIVITY_USERNAME, username)
                // .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }
}
