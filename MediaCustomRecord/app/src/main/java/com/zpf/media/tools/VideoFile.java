package com.zpf.media.tools;

import android.os.Environment;

import java.io.File;

/**
 * author:zpf
 * date:2019-08-16
 */
public class VideoFile {

    private static final String VIDEO_NAME = "/testvideo.mp4";
    public static final String VIDEO_IMAGE_NAME = "screen.jpeg";
    private File mFile;
    public VideoFile( ) {
    }

    public String getFullPath() {
        if(mFile == null){
            mFile = getFile();
        }
        return mFile.getAbsolutePath();
    }

    public File getFile() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + VIDEO_NAME);
        if (file.exists()) {
            // 如果文件存在，删除它，保证设备上只有一个文件
            file.delete();
        }
        return file;
    }

}
