package com.sandrios.sandriosCamera.internal.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by MartinRGB on 2017/1/18.
 */

public class CircleProgressView extends View {

    private static final int START_ANGLE_POINT = -90;

    private final Paint paint;
    private final Paint basePaint;
    private final RectF rect;

    private float angle;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final int strokeWidth = 8;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        //Circle color
        paint.setColor(Color.WHITE);

        basePaint = new Paint();
        basePaint.setAntiAlias(true);
        basePaint.setStyle(Paint.Style.STROKE);
        basePaint.setStrokeWidth(strokeWidth);
        basePaint.setStrokeCap(Paint.Cap.ROUND);
        basePaint.setColor(Color.WHITE);
        basePaint.setAlpha(122);

        //size 200x200 example
        rect = new RectF(strokeWidth, strokeWidth, 240 + strokeWidth, 240 + strokeWidth);

        //Initial Angle (optional, it can be zero)
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
        canvas.drawArc(rect, -90, 360, false, basePaint);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}