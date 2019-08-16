package com.zpf.media.camera;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import com.zpf.media.tools.Utils;

import java.io.IOException;
import java.util.List;

/**
 * author:zpf
 * date:2019-08-16
 */
public class CameraWrapper {
    private final int mDisplayRotation;
    private NativeCamera mNativeCamera = null;
    private CaptureConfiguration mConfig;

    public CameraWrapper(NativeCamera nativeCamera, int displayRotation,CaptureConfiguration config) {
        mNativeCamera = nativeCamera;
        mDisplayRotation = displayRotation;
        this.mConfig = config;
    }

    public Camera getCamera() {
        return mNativeCamera.getNativeCamera();
    }

    public void openCamera() throws OpenCameraException {
        try {
            mNativeCamera.openNativeCamera();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new OpenCameraException(OpenCameraException.OpenType.INUSE);
        }

        if (mNativeCamera.getNativeCamera() == null) {
            throw new OpenCameraException(OpenCameraException.OpenType.NOCAMERA);
        }
    }

    public void prepareCameraForRecording() throws PrepareCameraException {
        try {
            mNativeCamera.unlockNativeCamera();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw new PrepareCameraException();
        }
    }

    public void releaseCamera() {
        if (getCamera() == null) {
            return;
        }
        mNativeCamera.releaseNativeCamera();
    }

    public void startPreview(final SurfaceHolder holder) throws IOException {
        mNativeCamera.setNativePreviewDisplay(holder);
        mNativeCamera.startNativePreview();
    }

    public void stopPreview() throws Exception {
        mNativeCamera.stopNativePreview();
        mNativeCamera.clearNativePreviewCallback();
    }

    public RecordingSize getSupportedRecordingSize(int width, int height) {
        CameraSize recordingSize = getOptimalSize(getSupportedVideoSizes(Build.VERSION.SDK_INT), width, height);
        if (recordingSize == null) {
            return new RecordingSize(width, height);
        }
        return new RecordingSize(recordingSize.getWidth(), recordingSize.getHeight());
    }

    public CamcorderProfile getBaseRecordingProfile() {
        CamcorderProfile returnProfile;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            returnProfile = getDefaultRecordingProfile();
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            returnProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            returnProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        } else {
            returnProfile = getDefaultRecordingProfile();
        }
        return returnProfile;
    }

    private CamcorderProfile getDefaultRecordingProfile() {
        CamcorderProfile highProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        if (highProfile != null) {
            return highProfile;
        }
        CamcorderProfile lowProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        if (lowProfile != null) {
            return lowProfile;
        }
        throw new RuntimeException("No quality level found");
    }

    /**
     * 相机设置
     * @param viewWidth
     * @param viewHeight
     */
    public void configureForPreview(int viewWidth, int viewHeight) {
        final Camera.Parameters params = mNativeCamera.getNativeCameraParameters();
        final CameraSize previewSize = getOptimalSize(params.getSupportedPreviewSizes(), Math.max(viewWidth
                , viewHeight), Math.min(viewWidth, viewHeight));
        if(!Utils.checkNotNull(previewSize)){
            Log.e("liucheng",previewSize.getWidth() + "======" +previewSize.getHeight());
            params.setPreviewSize(mConfig.getVideoWidth(),mConfig.getVideoHeight());
            params.setPreviewFormat(ImageFormat.NV21);
            mNativeCamera.updateNativeCameraParameters(params);
            mNativeCamera.setDisplayOrientation(getRotationCorrection());
        }
    }

    public void enableAutoFocus() {
        final Camera.Parameters params = mNativeCamera.getNativeCameraParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mNativeCamera.updateNativeCameraParameters(params);
    }

    public int getRotationCorrection() {
        int displayRotation = mDisplayRotation * 90;
        return (mNativeCamera.getCameraOrientation() - displayRotation + 360) % 360;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected List<Camera.Size> getSupportedVideoSizes(int currentSdkInt) {
        Camera.Parameters params = mNativeCamera.getNativeCameraParameters();

        List<Camera.Size> supportedVideoSizes;
        if (currentSdkInt < Build.VERSION_CODES.HONEYCOMB) {
            supportedVideoSizes = params.getSupportedPreviewSizes();
        } else if (params.getSupportedVideoSizes() == null) {
            supportedVideoSizes = params.getSupportedPreviewSizes();
        } else {
            supportedVideoSizes = params.getSupportedVideoSizes();
        }

        return supportedVideoSizes;
    }

    /**
     * 获取最适显示比例
     * Copyright (C) 2013 The Android Open Source Project
     * <p/>
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     * <p/>
     * http://www.apache.org/licenses/LICENSE-2.0
     * <p/>
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    public CameraSize getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        final double targetRatio = (double) w / h;
        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        final int targetHeight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (final Camera.Size size : sizes) {
            final double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (final Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        if(!Utils.checkNotNull(optimalSize)){
            return new CameraSize(optimalSize.width, optimalSize.height);
        }
        return null;
    }


    public void setPreviewCallback(Camera.PreviewCallback callback){
        mNativeCamera.setNativePreViewCallBack(callback);
    }
}
