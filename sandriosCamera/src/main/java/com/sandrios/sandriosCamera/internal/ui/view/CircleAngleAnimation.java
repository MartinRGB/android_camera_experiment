package com.sandrios.sandriosCamera.internal.ui.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by MartinRGB on 2017/1/18.
 */

public class CircleAngleAnimation extends Animation {

    private CircleProgressView circle;

    private float oldAngle;
    private float newAngle;

    public CircleAngleAnimation(CircleProgressView circle, int newAngle) {
        this.oldAngle = circle.getAngle();
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

        circle.setAngle(angle);
        circle.requestLayout();
    }
}
