package com.zpf.media.video;

import android.content.Context;

import android.view.SurfaceHolder;

import com.netposa.media.encodersdk;
import com.zpf.media.MyApplication;
import com.zpf.media.audio.BaseRecoder;
import com.zpf.media.audio.GetPCMData;
import com.zpf.media.camera.CameraWrapper;
import com.zpf.media.camera.CaptureConfiguration;
import com.zpf.media.camera.OpenCameraException;
import com.zpf.media.camera.PrepareCameraException;
import com.zpf.media.tools.CLog;
import com.zpf.media.tools.VideoFile;
import com.zpf.media.tools.ViewUtils;

/**
 * author:zpf
 * date:2019-08-16
 */
public class VideoRecorder implements CapturePreviewInterface  {

    private CameraWrapper mCameraWrapper;
    private CapturePreview mVideoCapturePreview;

    private final CaptureConfiguration mCaptureConfiguration;
    private final VideoFile mVideoFile;

    private boolean mRecording = false;
    private final VideoRecorderInterface mRecorderInterface;

    private encodersdk mSdk;
    private BaseRecoder mAudioRecoder;
    private Context mContext;


    public VideoRecorder(VideoRecorderInterface recorderInterface, CaptureConfiguration captureConfiguration,
                         VideoFile videoFile, CameraWrapper cameraWrapper, SurfaceHolder previewHolder) {

        mCaptureConfiguration = captureConfiguration;
        mRecorderInterface = recorderInterface;
        mVideoFile = videoFile;
        mCameraWrapper = cameraWrapper;
        try {
            mSdk = new encodersdk();
        } catch (Exception e) {
            ViewUtils.showToast(MyApplication.getAppContext(), "暂时不支持小视频");
        }
        initializeCameraAndPreview(previewHolder);
    }

    /**
     * 打开相机
     *
     * @param previewHolder
     */
    protected void initializeCameraAndPreview(SurfaceHolder previewHolder) {
        try {
            mCameraWrapper.openCamera();
        } catch (final OpenCameraException e) {
            e.printStackTrace();
            mRecorderInterface.onRecordingFailed(e.getMessage());
            return;
        }

        mVideoCapturePreview = new CapturePreview(this, mCameraWrapper, previewHolder, mSdk, mCaptureConfiguration);
    }

    public void toggleRecording() {
        if (mCameraWrapper == null) {
            return;
        }
        startRecording();
    }

    protected void startRecording() {
        mRecording = false;

//        if (!initRecorder()) return;
        if (!startRecorder()) {
            return;
        }

        mRecording = true;
        CLog.d(CLog.RECORDER, "Successfully started recording - outputfile: " + mVideoFile.getFullPath());
    }

    public void stopRecording(String message) {
        if (!isRecording()) {
            return;
        }

        try {
            //停止
            mSdk.Stop();
            CloseAudioRecord();

            mRecorderInterface.onRecordingSuccess();
            CLog.d(CLog.RECORDER, "Successfully stopped recording - outputfile: " + mVideoFile.getFullPath());
        } catch (final RuntimeException e) {
            CLog.d(CLog.RECORDER, "Failed to stop recording");
        }
        mRecorderInterface.onRecordingStopped(message);
        mRecording = false;
    }

    private boolean initRecorder() {
        try {
            mCameraWrapper.prepareCameraForRecording();
        } catch (final PrepareCameraException e) {
            e.printStackTrace();
            mRecorderInterface.onRecordingFailed("Unable to record video");
            CLog.e(CLog.RECORDER, "Failed to initialize recorder - " + e.toString());
            return false;
        }

        //TODO 初始化
        configureMediaRecorder(mCameraWrapper.getCamera());
        return true;
    }

    private AudioInterface.EncodeListener mEncodeListener = new AudioInterface.EncodeListener() {

        @Override
        public void onEncode(long t, byte[] buffer, int length) {

        }

        @Override
        public void PushPCMData(long t, short[] buffer, int length) {
            if (isRecording()) {
                if (mSdk != null) {
                    //Log.i(TAG, "PushPCMStream Len is "+String.valueOf(length)+"");
                    if (buffer != null && length > 0) {
                        mSdk.pushPCMStream(buffer, length, 0);
                    }
                } else {
                    CloseAudioRecord();
                }
            }
        }
    };

    private void OpenRecord() {
        if (mAudioRecoder == null) {
            mAudioRecoder = new GetPCMData(mEncodeListener);
            mAudioRecoder.setRunning(true);
            new Thread(mAudioRecoder).start();
        }
    }

    @SuppressWarnings("deprecation")
    protected void configureMediaRecorder(android.hardware.Camera camera) throws IllegalStateException, IllegalArgumentException {

    }


    private boolean startRecorder() {
        try {
            String path = mVideoFile.getFullPath();
            //开始录制
            OpenRecord();
            mSdk.Init(mCaptureConfiguration.getVideoWidth(), mCaptureConfiguration.getVideoHeight(),
                    mCaptureConfiguration.getFps(), mCaptureConfiguration.getVideoBitrate(), path);
            CLog.d(CLog.RECORDER, "MediaRecorder successfully started");
            return true;
        } catch (final IllegalStateException e) {
            e.printStackTrace();
            CLog.e(CLog.RECORDER, "MediaRecorder start failed - " + e.toString());
            return false;
        } catch (final RuntimeException e2) {
            e2.printStackTrace();
            CLog.e(CLog.RECORDER, "MediaRecorder start failed - " + e2.toString());
            mRecorderInterface.onRecordingFailed("Unable to record video with given settings");
            return false;
        }
    }

    public boolean isRecording() {
        return mRecording;
    }

    /**
     * 关闭音频录制
     */
    private void CloseAudioRecord() {
        if (mAudioRecoder != null) {
            mAudioRecoder.setRunning(false);
            mAudioRecoder = null;
        }
    }


    public void releaseAllResources() {
        if (mVideoCapturePreview != null) {
            mVideoCapturePreview.releasePreviewResources();
        }

        if (mCameraWrapper != null) {
            mCameraWrapper.releaseCamera();
            mCameraWrapper = null;
        }
        CLog.d(CLog.RECORDER, "Released all resources");
    }

    @Override
    public void onCapturePreviewFailed() {
        mRecorderInterface.onRecordingFailed("Unable to show camera preview");
    }
}
