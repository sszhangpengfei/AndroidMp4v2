package com.zpf.media.video;

/**
 * author:zpf
 * date:2019-08-16
 */
public class AudioInterface {

    public static final int FRAME_SIZE_PCM_16BIT_MONO_8K = 160;

    public interface EncodeListener {
        public void onEncode(long t, byte[] buffer, int length);

        public void PushPCMData(long t, short[] buffer, int length);
    }

    public interface DecodeListener {
        public void onDecode(long t, short[] buffer, int length);
    }

    public interface IDecoder {

        public void putData(long t, byte[] buf, int size);

        public void setRunning(boolean isRunning);

        public boolean isRunning();

        public boolean isIdle();
    }

    public interface IEncoder {

        public void putData(short[] buf, int size);

        public void setRunning(boolean isRunning);

        public boolean isRunning();

        public boolean isIdle();
    }

    public enum AudioType {
        AMR(0), SPEEX(1),G711(3);
        private AudioType(int ni) {
            nativeInt = ni;
        }

        final int nativeInt;

        /**
         * 将枚举转换成对应的int值
         *
         * @return 对应的int值
         */
        public int value() {
            return this.nativeInt;
        }

        public static AudioType valueOf(int v) {
            for (AudioType t : AudioType.values()) {
                if (t.value() == v) {
                    return t;
                }
            }
            return null;
        }
    }


    public static final int CACHE_SIZE = 256;
    public static final int ENCODE_CACHE_SIZE = 10;
}
