package com.zpf.media.camera;

import android.media.MediaRecorder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * author:zpf
 * date:2019-08-16
 */

public class CaptureConfiguration implements Parcelable {

    private int mVideoWidth = PredefinedCaptureConfigurations.WIDTH_480P;
    private int mVideoHeight = PredefinedCaptureConfigurations.HEIGHT_480P;
    private int mBitrate = PredefinedCaptureConfigurations.BITRATE_HQ_480P;
    private int mFps = PredefinedCaptureConfigurations.FPS_480P;

    private int OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;
    private int AUDIO_ENCODER = MediaRecorder.AudioEncoder.AAC;
    private int VIDEO_SOURCE = MediaRecorder.VideoSource.CAMERA;
    private int VIDEO_ENCODER = MediaRecorder.VideoEncoder.H264;

    public CaptureConfiguration(PredefinedCaptureConfigurations.CaptureResolution resolution, PredefinedCaptureConfigurations.CaptureQuality quality) {
        mVideoWidth = resolution.width;
        mVideoHeight = resolution.height;
        mFps = resolution.fps;
        mBitrate = resolution.getBitrate(quality);
    }


    public CaptureConfiguration(int videoWidth, int videoHeight, int bitrate) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        mBitrate = bitrate;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getFps(){
        return mFps;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getVideoBitrate() {
        return mBitrate;
    }

    public int getOutputFormat() {
        return OUTPUT_FORMAT;
    }

    public int getAudioSource() {
        return AUDIO_SOURCE;
    }

    public int getAudioEncoder() {
        return AUDIO_ENCODER;
    }

    public int getVideoSource() {
        return VIDEO_SOURCE;
    }

    public int getVideoEncoder() {
        return VIDEO_ENCODER;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mVideoWidth);
        dest.writeInt(mVideoHeight);
        dest.writeInt(mBitrate);
        dest.writeInt(mFps);

        dest.writeInt(OUTPUT_FORMAT);
        dest.writeInt(AUDIO_SOURCE);
        dest.writeInt(AUDIO_ENCODER);
        dest.writeInt(VIDEO_SOURCE);
        dest.writeInt(VIDEO_ENCODER);
    }

    public static final Creator<CaptureConfiguration> CREATOR = new Creator<CaptureConfiguration>() {
        @Override
        public CaptureConfiguration createFromParcel(
                Parcel in) {
            return new CaptureConfiguration(in);
        }

        @Override
        public CaptureConfiguration[] newArray(
                int size) {
            return new CaptureConfiguration[size];
        }
    };

    private CaptureConfiguration(Parcel in) {
        mVideoWidth = in.readInt();
        mVideoHeight = in.readInt();
        mBitrate = in.readInt();
        mFps = in.readInt();

        OUTPUT_FORMAT = in.readInt();
        AUDIO_SOURCE = in.readInt();
        AUDIO_ENCODER = in.readInt();
        VIDEO_SOURCE = in.readInt();
        VIDEO_ENCODER = in.readInt();
    }

}
