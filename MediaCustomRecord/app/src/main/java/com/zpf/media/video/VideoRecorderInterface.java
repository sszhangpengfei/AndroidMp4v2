package com.zpf.media.video;

/**
 * author:zpf
 * date:2019-08-16
 */
public interface VideoRecorderInterface {

    void onRecordingSuccess();

    void onRecordingFailed(String message);

    void onRecordingStopped(String message);
}
