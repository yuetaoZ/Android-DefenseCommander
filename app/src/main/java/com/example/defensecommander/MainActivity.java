package com.example.defensecommander;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static int screenHeight;
    public static int screenWidth;
    private MissileMaker missileMaker;
    private ViewGroup layout;
    private ImageView base1, base2, base3;
    private int scoreValue;
    private TextView score, level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFullScreen();

        getScreenDimensions();

        setupImages();

        setupOnTouchListener();

        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
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
        screenWidth = displayMetrics.widthPixels;
    }

    private void setupImages() {
        layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.level);
        base1 = findViewById(R.id.base1);
        base2 = findViewById(R.id.base2);
        base3 = findViewById(R.id.base3);

        new CloudScroller(this, layout, R.drawable.clouds, 30000);
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

        if (x < screenWidth * 0.33) {
            launcher = findViewById(R.id.base1);
        } else if (x > screenWidth * 0.33 && x < screenWidth * 0.66) {
            launcher = findViewById(R.id.base2);
        } else {
            launcher = findViewById(R.id.base3);
        }

        double startX = launcher.getX() + (0.5 * launcher.getWidth());
        double startY = launcher.getY() + (0.5 * launcher.getHeight());

        Interceptor i = new Interceptor(this,  (float) (startX - 10), (float) (startY - 30), x, y);
        SoundPlayer.getInstance().start("launch_interceptor");
        i.launch();
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
}