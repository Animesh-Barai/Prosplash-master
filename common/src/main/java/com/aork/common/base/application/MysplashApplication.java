package com.aork.common.base.application;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.aork.common.base.activity.LoadableActivity;
import com.aork.common.base.activity.MysplashActivity;
import com.aork.base.unsplash.Photo;
import com.aork.common.utils.helper.CrashReportHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Mysplash application.
 * */

public abstract class MysplashApplication extends MultiModulesApplication {

    private static MysplashApplication instance;
    public static MysplashApplication getInstance() {
        return instance;
    }

    private List<MysplashActivity> activityList;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        activityList = new ArrayList<>();

        if (isDebug(this)) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);

        CrashReportHelper.init(this);
    }

    public static boolean isDebug(Context c) {
        try {
            return (c.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignored) {

        }
        return false;
    }

    public void addActivity(@NonNull MysplashActivity a) {
        activityList.add(a);
    }

    public void removeActivity(MysplashActivity a) {
        activityList.remove(a);
    }

    @Nullable
    public MysplashActivity getTopActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        } else {
            return null;
        }
    }

    @Nullable
    public MysplashActivity getSecondFloorActivity() {
        if (activityList != null && activityList.size() > 1) {
            return activityList.get(activityList.size() - 2);
        } else {
            return null;
        }
    }

    public int getActivityCount() {
        if (activityList != null) {
            return activityList.size();
        } else {
            return 0;
        }
    }

    public List<Photo> loadMorePhotos(MysplashActivity activity, int currentCount) {
        int index = activityList.indexOf(activity) - 1;
        if (index > -1) {
            Activity a = activityList.get(index);
            if (a instanceof LoadableActivity) {
                try {
                    if (((LoadableActivity) a).isValidProvider(Photo.class)) {
                        return ((LoadableActivity<Photo>) a).loadMoreData(currentCount);
                    }
                } catch (Exception ignored) {
                    // do nothing.
                }
            }
        }
        return new ArrayList<>();
    }

    public void dispatchRecreate() {
        for (int i = activityList.size() - 1; i >= 0; i --) {
            activityList.get(i).recreate();
        }
    }
}
