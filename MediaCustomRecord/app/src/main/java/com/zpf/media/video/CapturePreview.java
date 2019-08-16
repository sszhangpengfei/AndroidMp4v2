package com.zpf.media.video;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.netposa.media.encodersdk;
import com.zpf.media.camera.CameraWrapper;
import com.zpf.media.camera.CaptureConfiguration;
import com.zpf.media.tools.CLog;

import java.io.IOException;

/**
 * author:zpf
 * date:2019-08-16
 */
public class CapturePreview implements SurfaceHolder.Callback, android.hardware.Camera.PreviewCallback{

    private boolean mPreviewRunning = false;
    private final CapturePreviewInterface mInterface;
    public final CameraWrapper mCameraWrapper;
    private encodersdk mSdk;

    private long time;
    private CaptureConfiguration mCaptureConfiguration;

    public CapturePreview(CapturePreviewInterface capturePreviewInterface, CameraWrapper cameraWrapper,
                          SurfaceHolder holder,encodersdk sdk,CaptureConfiguration captureConfiguration) {
        mInterface = capturePreviewInterface;
        mCameraWrapper = cameraWrapper;
        this.mSdk = sdk;
        this.mCaptureConfiguration = captureConfiguration;
        initalizeSurfaceHolder(holder);
    }

    @SuppressWarnings("deprecation")
    private void initalizeSurfaceHolder(final SurfaceHolder surfaceHolder) {
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // Necessary for older API's
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        // NOP
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
        if (mPreviewRunning) {
            try {
                mCameraWrapper.stopPreview();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        try {
            mCameraWrapper.configureForPreview(width, height);
            CLog.d(CLog.PREVIEW, "Configured camera for preview in surface of " + width + " by " + height);
        } catch (final RuntimeException e) {
            e.printStackTrace();
            CLog.d(CLog.PREVIEW, "Failed to show preview - invalid parameters set to camera preview");
            mInterface.onCapturePreviewFailed();
            return;
        }

        try {
            mCameraWrapper.enableAutoFocus();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            CLog.d(CLog.PREVIEW, "AutoFocus not available for preview");
        }

        try {
            mCameraWrapper.setPreviewCallback(this);
            mCameraWrapper.startPreview(holder);
            setPreviewRunning(true);
        } catch (final IOException e) {
            e.printStackTrace();
            CLog.d(CLog.PREVIEW, "Failed to show preview - unable to connect camera to preview (IOException)");
            mInterface.onCapturePreviewFailed();
        } catch (final RuntimeException e) {
            e.printStackTrace();
            CLog.d(CLog.PREVIEW, "Failed to show preview - unable to start camera preview (RuntimeException)");
            mInterface.onCapturePreviewFailed();
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        // NOP
    }

    public void releasePreviewResources() {
        if (mPreviewRunning) {
            try {
                mCameraWrapper.stopPreview();
                setPreviewRunning(false);
            } catch (final Exception e) {
                e.printStackTrace();
                CLog.e(CLog.PREVIEW, "Failed to clean up preview resources");
            }
        }
    }

    protected void setPreviewRunning(boolean running) {
        mPreviewRunning = running;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        time += mCaptureConfiguration.getVideoBitrate()/mCaptureConfiguration.getFps();
        byte[] yuv420 = new byte[mCaptureConfiguration.getVideoWidth()*mCaptureConfiguration.getVideoHeight()*3/2];
        YUV420SP2YUV420(data,yuv420,mCaptureConfiguration.getVideoWidth(),mCaptureConfiguration.getVideoHeight());
        mSdk.PushOriStream(yuv420,yuv420.length,time);
    }


    private void YUV420SP2YUV420(byte[] yuv420sp, byte[] yuv420, int width, int height)
    {
        if (yuv420sp == null ||yuv420 == null) {
            return;
        }
        int framesize = width*height;
        int i = 0, j = 0;
        //copy y
        for (i = 0; i < framesize; i++)
        {
            yuv420[i] = yuv420sp[i];
        }
        i = 0;
        for (j = 0; j < framesize/2; j+=2)
        {
            yuv420[i + framesize*5/4] = yuv420sp[j+framesize];
            i++;
        }
        i = 0;
        for(j = 1; j < framesize/2;j+=2)
        {
            yuv420[i+framesize] = yuv420sp[j+framesize];
            i++;
        }
    }

}
