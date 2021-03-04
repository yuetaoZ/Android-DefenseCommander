package com.example.defensecommander;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        hideSystemUI();

        fadeInTitle();

        setupBackGroundSound();

        startMainActivity();

    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void fadeInTitle() {
        ImageView titleImageView= findViewById(R.id.titleImage);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        titleImageView.startAnimation(myFadeInAnimation);
    }

    private void setupBackGroundSound() {
        SoundPlayer sp = SoundPlayer.getInstance();
        sp.setupSound(this, "background", R.raw.background, true);
        sp.setupSound(this, "base_blast", R.raw.base_blast, false);
        sp.setupSound(this, "interceptor_blast", R.raw.interceptor_blast, false);
        sp.setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile, false);
        sp.setupSound(this, "launch_interceptor", R.raw.launch_interceptor, false);
        sp.setupSound(this, "launch_missile", R.raw.launch_missile, false);
        sp.setupSound(this, "missile_miss", R.raw.missile_miss, false);
    }

    private void startMainActivity() {
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
