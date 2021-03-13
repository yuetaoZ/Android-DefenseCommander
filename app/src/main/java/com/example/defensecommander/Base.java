package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;

public class Base {
    private float x;
    private float y;
    private ImageView imageView;
    private MainActivity mainActivity;
    private ArrayList<Base> activeBases;

    public Base(MainActivity mainActivity, ImageView view, ArrayList<Base> activeBases) {
        this.mainActivity = mainActivity;
        this.imageView = view;
        this.activeBases = activeBases;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void baseBlast() {
        SoundPlayer.getInstance().start("base_blast");
        mainActivity.getLayout().removeView(this.getImageView());
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.blast);

        explodeView.setTransitionName("Base blast");

        float w = explodeView.getDrawable().getIntrinsicWidth();
        explodeView.setX(this.getX() - (w/2));

        explodeView.setY(this.getY() - (w/2));

        explodeView.setZ(-2);

        mainActivity.getLayout().addView(explodeView);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(explodeView);
            }
        });
        alpha.start();


        activeBases.remove(this);

        if (activeBases.size() == 0) mainActivity.gameOver();
    }
}
