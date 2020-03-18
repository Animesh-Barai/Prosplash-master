package com.aork.prosplash;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.aork.prosplash.main.MainActivity;

public class appintro extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(AppIntroFragment.newInstance("welcome to the app", "this app is at ur service for tje job", R.drawable.button_join, ContextCompat.getColor(getApplicationContext(),R.color.colorAccent)));
        addSlide(AppIntroFragment.newInstance("welcome to the app", "this app is at ur service for tje job", R.drawable.button_login, ContextCompat.getColor(getApplicationContext(),R.color.colorNotification)));
        addSlide(AppIntroFragment.newInstance("welcome to the app", "this app is at ur service for tje job", R.drawable.ic_arrow_back_white, ContextCompat.getColor(getApplicationContext(),R.color.colorAccentRed)));

    }  @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        // Do something when users tap on Done button.
    }
}
