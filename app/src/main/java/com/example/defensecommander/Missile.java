package com.example.defensecommander;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

class Missile {

    private final MainActivity mainActivity;
    private final ImageView imageView;
    private final AnimatorSet aSet = new AnimatorSet();
    private final int screenHeight;
    private final int screenWidth;
    private final long screenTime;
    private static final String TAG = "Missile";
    private boolean hit = false;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;


        imageView = new ImageView(mainActivity);
        imageView.setY(-200);
        imageView.setZ(-2);

        mainActivity.runOnUiThread(() -> mainActivity.getLayout().addView(imageView));

    }

    AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(() -> imageView.setImageResource(drawId));

        int startX = (int) (Math.random() * screenWidth);
        int endX = (int) (Math.random() * screenWidth);
        int startY = -200;
        int endY = screenHeight - 100;

        float a = calculateAngle(startX, startY, endX, endY);
        imageView.setRotation(a);


        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);
        xAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.runOnUiThread(() -> {
                    if (!hit) {
                        interceptorBlast(imageView.getX(), imageView.getY());
                        mainActivity.removeMissile(Missile.this);
                    }
                    Log.d(TAG, "run: NUM VIEWS " +
                            mainActivity.getLayout().getChildCount());
                });

            }
        });

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);

        aSet.playTogether(xAnim, yAnim);
        return aSet;

    }

    void stop() {
        aSet.cancel();
    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }

    void setHit(boolean b) { hit = b; }

    void interceptorBlast(float x, float y) {

        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setZ(-2);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.getLayout().removeView(imageView);
        mainActivity.getLayout().addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.getLayout().removeView(imageView);
                mainActivity.getLayout().removeView(iv);
            }
        });
        alpha.start();
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (180.0f - angle);
    }
}
