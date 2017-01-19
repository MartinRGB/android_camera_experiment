package com.sandrios.sandriosCamera.internal.ui;

/**
 * Created by MartinRGB on 2017/1/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sandrios.sandriosCamera.R;

public class DetectionView extends View
{

    private Paint paint = null;
    private Rect mRect = null;
    private Context mContext;
    private Paint mStarOnePaint;
    private Paint mStarTwoPaint;

    // Calculate our alpha step from our fade parameters
    private static final int FADE_MILLISECONDS = 3000; // 3 second fade effect
    private static final int FADE_STEP = 60;          // 120ms refresh
    private static final int ALPHA_STEP = 255 / (FADE_MILLISECONDS / FADE_STEP);
    // Need to keep track of the current alpha value
    private int currentAlpha = 255;

    public void updatePosition(Rect rect){
        mRect = rect;
        postInvalidate();
    }

    public DetectionView(Context context)
    {
        super(context);
        init(context);
    }

    public DetectionView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public DetectionView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs,defStyle);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        mStarOnePaint = new Paint();
        mStarOnePaint.setAlpha(currentAlpha);

        mStarTwoPaint = new Paint();
        mStarTwoPaint.setAlpha(currentAlpha-200);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(mRect != null){


            adjustmentMatrix(canvas,mRect,paint);


        }

    }

    public void adjustmentMatrix(Canvas canvas,Rect rect,Paint paint) {

        //Old Style - Source:http://bytefish.de/blog/face_detection_with_android/
        Matrix matrix = new Matrix();
        Matrix matrix2 = new Matrix();
        Matrix matrix3 = new Matrix();
        canvas.save();
        matrix.postRotate(90);
        canvas.concat(matrix);
        matrix2.setScale(-0.96f,0.54f);
        canvas.concat(matrix2);
        matrix3.postTranslate(-1920 / 2f,-1920 / 2f );
        canvas.concat(matrix3);
        canvas.drawRoundRect(rect.left, rect.top, rect.right, rect.bottom, 20f,40f,paint);
        canvas.restore();



    }



}