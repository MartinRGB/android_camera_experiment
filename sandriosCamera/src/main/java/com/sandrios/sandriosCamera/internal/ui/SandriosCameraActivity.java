package com.sandrios.sandriosCamera.internal.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.google.android.gms.vision.CameraSource;
import com.sandrios.sandriosCamera.R;
import com.sandrios.sandriosCamera.internal.configuration.ConfigurationProvider;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;
import com.sandrios.sandriosCamera.internal.controller.CameraController;
import com.sandrios.sandriosCamera.internal.controller.impl.Camera1Controller;
import com.sandrios.sandriosCamera.internal.controller.view.CameraView;
import com.sandrios.sandriosCamera.internal.ui.view.AspectFrameLayout;
import com.sandrios.sandriosCamera.internal.ui.view.AutoFitSurfaceView;
import com.sandrios.sandriosCamera.internal.utils.Size;
import com.sandrios.sandriosCamera.internal.utils.Utils;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.graphics.Color.*;

/**
 * Created by Arpit Gandhi on 12/1/16.
 */

abstract public class SandriosCameraActivity<CameraId> extends AutoLayoutActivity
        implements ConfigurationProvider, CameraView, SensorEventListener {

    protected AspectFrameLayout previewContainer;
    protected ViewGroup userContainer;
    @CameraConfiguration.SensorPosition
    protected int sensorPosition = CameraConfiguration.SENSOR_POSITION_UNSPECIFIED;
    @CameraConfiguration.DeviceDefaultOrientation
    protected int deviceDefaultOrientation;
    private SensorManager sensorManager = null;
    private CameraController<CameraId> cameraController;
    private int degrees = -1;

    private Context mContext;
    private DetectionView mLockView;
    private static final SpringConfig alphaconfig = SpringConfig.fromOrigamiTensionAndFriction(160, 20);
    private SpringSystem mSpringSystem;
    public Spring mSpringsAlpha;
    boolean rectHasDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraController = createCameraController(this, this);
        cameraController.onCreate(savedInstanceState);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        int defaultOrientation = Utils.getDeviceDefaultOrientation(this);

        if (defaultOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            deviceDefaultOrientation = CameraConfiguration.ORIENTATION_LANDSCAPE;
        } else if (defaultOrientation == Configuration.ORIENTATION_PORTRAIT) {
            deviceDefaultOrientation = CameraConfiguration.ORIENTATION_PORTRAIT;
        }

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT > 15) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

        setContentView(R.layout.generic_camera_layout);

        previewContainer = (AspectFrameLayout) findViewById(R.id.previewContainer);
        userContainer = (ViewGroup) findViewById(R.id.userContainer);
        mLockView = (DetectionView) findViewById(R.id.lockOverlay);

        onProcessBundle(savedInstanceState);
        setUserContent();

        mContext = getApplicationContext();

        setSpringSystem();

        cameraController.setRectChangeListener(new Camera1Controller.RectChangeListener() {
            @Override
            public void RectChange(Rect rect) {
                if(rect !=null){

                    mLockView.updatePosition(rect);
                    mSpringsAlpha.setCurrentValue(1);

                }
            }

            @Override
            public void RectDetected(Boolean mboolean) {
                rectHasDetect = mboolean;
                mSpringsAlpha.setEndValue(0);

            }
        });

    }

    private void setSpringSystem() {
        mSpringSystem = SpringSystem.create();
        mSpringsAlpha = mSpringSystem.createSpring()
                .setSpringConfig(alphaconfig)
                .setCurrentValue(1);

        mSpringsAlpha.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring mSpring) {
                float value = (float) mSpring.getCurrentValue();
                mLockView.setAlpha(value);
            }
        });


    }

    protected void onProcessBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraController.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);



    }

    @Override
    protected void onPause() {
        super.onPause();

        cameraController.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public final CameraController<CameraId> getCameraController() {
        return cameraController;
    }

    public abstract CameraController<CameraId> createCameraController(CameraView cameraView, ConfigurationProvider configurationProvider);

    private void setUserContent() {
        userContainer.removeAllViews();
        userContainer.addView(getUserContentView(LayoutInflater.from(this), userContainer));
    }

    public final void setCameraPreview(View preview, Size previewSize) {
        onCameraControllerReady();

        if (previewContainer == null || preview == null) return;
        previewContainer.removeAllViews();
        previewContainer.addView(preview);

        previewContainer.setAspectRatio(previewSize.getHeight() / (double) previewSize.getWidth());
    }

    public final void clearCameraPreview() {
        if (previewContainer != null)
            previewContainer.removeAllViews();
    }

    abstract View getUserContentView(LayoutInflater layoutInflater, ViewGroup parent);

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                if (sensorEvent.values[0] < 4 && sensorEvent.values[0] > -4) {
                    if (sensorEvent.values[1] > 0) {
                        // UP
                        sensorPosition = CameraConfiguration.SENSOR_POSITION_UP;
                        degrees = deviceDefaultOrientation == CameraConfiguration.ORIENTATION_PORTRAIT ? 0 : 90;
                    } else if (sensorEvent.values[1] < 0) {
                        // UP SIDE DOWN
                        sensorPosition = CameraConfiguration.SENSOR_POSITION_UP_SIDE_DOWN;
                        degrees = deviceDefaultOrientation == CameraConfiguration.ORIENTATION_PORTRAIT ? 180 : 270;
                    }
                } else if (sensorEvent.values[1] < 4 && sensorEvent.values[1] > -4) {
                    if (sensorEvent.values[0] > 0) {
                        // LEFT
                        sensorPosition = CameraConfiguration.SENSOR_POSITION_LEFT;
                        degrees = deviceDefaultOrientation == CameraConfiguration.ORIENTATION_PORTRAIT ? 90 : 180;
                    } else if (sensorEvent.values[0] < 0) {
                        // RIGHT
                        sensorPosition = CameraConfiguration.SENSOR_POSITION_RIGHT;
                        degrees = deviceDefaultOrientation == CameraConfiguration.ORIENTATION_PORTRAIT ? 270 : 0;
                    }
                }
                onScreenRotation(degrees);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public final int getSensorPosition() {
        return sensorPosition;
    }

    @Override
    public final int getDegrees() {
        return degrees;
    }

    protected abstract void onScreenRotation(int degrees);

    protected void onCameraControllerReady() {
    }
}
