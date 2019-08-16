package com.zpf.media.audio;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import com.zpf.media.video.AudioInterface;

import static com.zpf.media.video.AudioInterface.FRAME_SIZE_PCM_16BIT_MONO_8K;

/**
 * author:zpf
 * date:2019-08-16
 */
public class GetPCMData extends BaseRecoder{

    private AudioInterface.EncodeListener consumer;

    private volatile boolean isRunning;

    private final Object mutex = new Object();

    private static int frequency = 8000;
    private static int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recordInstance;
    private short[] mBuffer;
    private int mChannelConfig;
    public GetPCMData(AudioInterface.EncodeListener l) {
        this.consumer = l;
    }

    @Override
    public void setRunning(boolean isRunning) {
        synchronized (mutex) {
            this.isRunning = isRunning;
            if (this.isRunning) {
                mutex.notify();
            }
            else {
                recordInstance.stop();
                recordInstance.release();
                recordInstance = null;
            }
        }
    }

    @Override
    public boolean isRunning() {
        synchronized (mutex) {
            return isRunning;
        }
    }

    private boolean isPause = false;

    @Override
    public void setPause(boolean isPause) {
        synchronized (mutex) {
            this.isPause = isPause;
        }
    }

    @Override
    public boolean isPause() {
        synchronized (mutex) {
            return isPause;
        }
    }

    @Override
    public boolean checkType(String type) {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void run() {

        android.os.Process
                .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        int bufferRead = 0;
        createAudioRecord();
        try {
            recordInstance.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
            Log.e("IllegalStateException","AudioRecord start fail");
        }

        while (this.isRunning) {
            bufferRead = recordInstance.read(mBuffer, 0,
                    FRAME_SIZE_PCM_16BIT_MONO_8K);
            if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
                continue;
            } else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
                continue;
            }
            if (!isPause) {
                consumer.PushPCMData(System.currentTimeMillis(), mBuffer, bufferRead);
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    private void createAudioRecord() {
        for (int sampleRate : new int[] { 8000, 11025, 16000, 22050, 32000,
                44100, 47250, 48000 }) {
            for (short audioFormat : new short[] {
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioFormat.ENCODING_PCM_8BIT }) {
                for (short channelConfig : new short[] {
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.CHANNEL_CONFIGURATION_STEREO }) {

                    // Try to initialize
                    try {
                        int mBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                                channelConfig, audioFormat);

                        if (mBufferSize < 0) {
                            continue;
                        }

                        mBuffer = new short[mBufferSize];
                        recordInstance = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                sampleRate, channelConfig, audioFormat,
                                mBufferSize);

                        if (recordInstance.getState() == AudioRecord.STATE_INITIALIZED) {
                            frequency = sampleRate;
                            audioEncoding = audioFormat;
                            mChannelConfig = channelConfig;
                            return;
                        }

                        recordInstance.release();
                        recordInstance = null;
                    } catch (Exception e) {
                        // Do nothing
                        recordInstance.release();
                        recordInstance = null;
                    }
                }
            }
        }

        // ADDED(billhoo) all combinations are failed on this device.
        throw new IllegalStateException(
                "getInstance() failed : no suitable audio configurations on this device.");
    }

}
