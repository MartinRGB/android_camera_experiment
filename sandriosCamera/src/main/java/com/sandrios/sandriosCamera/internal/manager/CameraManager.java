package com.sandrios.sandriosCamera.internal.manager;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;

import com.sandrios.sandriosCamera.internal.configuration.ConfigurationProvider;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;
import com.sandrios.sandriosCamera.internal.manager.impl.Camera1Manager;
import com.sandrios.sandriosCamera.internal.manager.listener.CameraCloseListener;
import com.sandrios.sandriosCamera.internal.manager.listener.CameraOpenListener;
import com.sandrios.sandriosCamera.internal.manager.listener.CameraPhotoListener;
import com.sandrios.sandriosCamera.internal.manager.listener.CameraVideoListener;
import com.sandrios.sandriosCamera.internal.utils.Size;

import java.io.File;

/**
 * Created by Arpit Gandhi on 8/14/16.
 */
public interface CameraManager<CameraId, SurfaceListener> {

    void initializeCameraManager(ConfigurationProvider configurationProvider, Context context);

    void openCamera(CameraId cameraId, CameraOpenListener<CameraId, SurfaceListener> cameraOpenListener);

    void closeCamera(CameraCloseListener<CameraId> cameraCloseListener);

    void takePhoto(File photoFile, CameraPhotoListener cameraPhotoListener);

    void startVideoRecord(File videoFile, CameraVideoListener cameraVideoListener);

    Size getPhotoSizeForQuality(@CameraConfiguration.MediaQuality int mediaQuality);

    void setFlashMode(@CameraConfiguration.FlashMode int flashMode);

    void stopVideoRecord();

    void setOnRectChangeListener(Camera1Manager.OnRectChangeListener onRectChangeListener);

    void releaseCameraManager();

    Rect getRect();

    CameraId getCurrentCameraId();

    CameraId getFaceFrontCameraId();

    CameraId getFaceBackCameraId();

    int getNumberOfCameras();

    int getFaceFrontCameraOrientation();

    int getFaceBackCameraOrientation();

    boolean isVideoRecording();
}
