package com.example.defensecommander;

import android.widget.ImageView;

public class Base {
    private float x;
    private float y;
    private ImageView imageView;

    public Base(ImageView view) {
        this.imageView = view;
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
}
