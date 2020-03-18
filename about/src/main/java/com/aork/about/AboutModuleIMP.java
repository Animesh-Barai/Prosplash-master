package com.aork.about;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.aork.about.activity.AboutActivity;
import com.aork.about.activity.IntroduceActivity;
import com.aork.component.module.AboutModule;

public class AboutModuleIMP implements AboutModule {

    @Override
    public void startAboutActivity(Activity a) {
        ARouter.getInstance()
                .build(AboutActivity.ABOUT_ACTIVITY)
                .withTransition(R.anim.activity_slide_in, R.anim.none)
                .navigation(a);
    }

    @Override
    public void checkAndStartIntroduce(Activity a) {
        IntroduceActivity.checkAndStartIntroduce(a);
    }

    @Override
    public void watchAllIntroduce(Activity a) {
        IntroduceActivity.watchAllIntroduce(a);
    }
}
