package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int screenHeight;
    private int screenWidth;
    private float leftScreenPart, midScreenPart, rightScreenPart;
    private Base base1, base2, base3;
    private MissileMaker missileMaker;
    private ViewGroup layout;
    private final ArrayList<Base> activeBases = new ArrayList<>();
    private int scoreValue;
    private TextView score, level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFullScreen();

        getScreenDimensions();

        setupImages();

        setupBases();

        setupOnTouchListener();

        launchMissileMaker();

    }

    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels + getBarHeight();
        leftScreenPart = (float) (screenWidth * 0.33);
        midScreenPart = (float) (screenWidth * 0.5);
        rightScreenPart = (float) (screenWidth * 0.66);
    }

    private void setupImages() {
        layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.level);

        new CloudScroller(this, layout, R.drawable.clouds, 30000, screenHeight, screenWidth);
    }

    private void setupBases() {
        base1 = new Base(findViewById(R.id.base1));
        base1.setX((float) (screenWidth * 0.25));
        base1.setY(screenHeight);
        base2 = new Base(findViewById(R.id.base2));
        base2.setX((float) (screenWidth * 0.5));
        base2.setY(screenHeight);
        base3 = new Base(findViewById(R.id.base3));
        base3.setX((float) (screenWidth * 0.8));
        base3.setY(screenHeight);

        activeBases.add(base1);
        activeBases.add(base2);
        activeBases.add(base3);
    }

    public ArrayList<Base> getActiveBases() {
        return activeBases;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupOnTouchListener() {
        layout.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(motionEvent.getX(), motionEvent.getY());
            }
            return false;
        });
    }

    private void handleTouch(float x, float y) {
        ImageView launcher;

        if (x < leftScreenPart) {
            if (activeBases.contains(base1)) {
                launcher = findViewById(R.id.base1);
            } else if (activeBases.contains(base2)) {
                launcher = findViewById(R.id.base2);
            } else {
                launcher = findViewById(R.id.base3);
            }
        } else if (x < rightScreenPart) {
            if (activeBases.contains(base2)) {
                launcher = findViewById(R.id.base2);
            } else {
                if (x < midScreenPart) {
                    if (activeBases.contains(base1)) {
                        launcher = findViewById(R.id.base1);
                    } else {
                        launcher = findViewById(R.id.base3);
                    }
                } else {
                    if (activeBases.contains(base3)) {
                        launcher = findViewById(R.id.base3);
                    } else {
                        launcher = findViewById(R.id.base1);
                    }
                }
            }
        } else {
            if (activeBases.contains(base3)) {
                launcher = findViewById(R.id.base3);
            } else if (activeBases.contains(base2)) {
                launcher = findViewById(R.id.base2);
            } else {
                launcher = findViewById(R.id.base1);
            }
        }

        double startX = launcher.getX() + (0.5 * launcher.getWidth());
        double startY = launcher.getY() + (0.5 * launcher.getHeight());

        Interceptor i = new Interceptor(this,  (float) (startX - 10), (float) (startY - 30), x, y);
        SoundPlayer.getInstance().start("launch_interceptor");
        i.launch();
    }

    private void launchMissileMaker() {
        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }

    public ViewGroup getLayout() {
        return layout;
    }

    public void incrementScore() {
        scoreValue++;
        score.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    public void setLevel(final int value) {
        runOnUiThread(() -> level.setText(String.format(Locale.getDefault(), "Level: %d", value)));
    }

    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        missileMaker.applyInterceptorBlast(interceptor, id);
    }

    public void removeMissile(Missile m) {
        missileMaker.removeMissile(m);
    }

    private int getBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public void gameOver() {
        missileMaker.setRunning(false);
        fadeInGameOverTitle();
        checkScore();
    }

    private void fadeInGameOverTitle() {
        ImageView titleImageView= findViewById(R.id.titleImageGameOver);
        titleImageView.setVisibility(View.VISIBLE);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        titleImageView.startAnimation(myFadeInAnimation);
    }

    private void checkScore() {
        // compare scoreValue with 10th score.
        String initials = "";
        int score = 0;
        int level = -1;
        TopScoreDatabaseHandler dbh =
                new TopScoreDatabaseHandler(this, initials, score, level);
        new Thread(dbh).start();
    }

    public void setResults(String s) {
        Intent intent = new Intent();
        intent.putExtra("DATA", s);

    }
}