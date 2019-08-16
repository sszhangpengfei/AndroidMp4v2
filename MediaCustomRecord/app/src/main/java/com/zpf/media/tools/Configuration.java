package com.zpf.media.tools;

import java.io.File;

/**
 * author:zpf
 * date:2019-08-16
 */
public class Configuration {

    private static final String TAG = Configuration.class.getSimpleName();
    private static String ROOT_PATH = Constants.DEFAULT_EXTERNAL_PATH + Constants.MAIN_PATH;

    /**
     * @brief 获取应用程序存储根目录目录
     */
    public static String getRootPath() {
        if(!FileUtil.fileExists(ROOT_PATH)){
            initDirectoryPath();
        }
        return ROOT_PATH;
    }

    /**
     * @brief 获取缓存目录
     */
    public static String getCacheDirectoryPath() {
        String path = getRootPath() + Constants.CACHE_PATH;
        FileUtil.mkdirs(path);
        return path;
    }

    /**
     * @brief 获取拍照存储目录
     * @return
     */
    public static String getCaptureDirectoryPath(){
        String path = getRootPath() + Constants.CAPTURE_PATH;
        FileUtil.mkdirs(path);
        return path;
    }

    /**
     * @brief 获取视频目录
     */
    public static String getVideoDirectoryPath() {
        String path = getRootPath() + Constants.VIDEO_PATH;
        FileUtil.mkdirs(path);
        return path;
    }



    /**
     * @brief 获取日志目录
     */
    public static String getLogDirectoryPath() {
        String path = getRootPath() + Constants.LOG_PATH;
        FileUtil.mkdirs(path);
        return path;
    }

    public static String getExternalROOTPath(){
        return DeviceUtil.getExternalStorage() + Constants.MAIN_PATH;
    }

    public static String getInternalROOTPath(){
        //return MyApplication.getInstance().getFilesDir() + Constants.MAIN_PATH;
        return DeviceUtil.getInternalFilesDir();
    }

    /**
     * 初始化应用目录
     */
    public static void initDirectoryPath() {
        if (DeviceUtil.hasSDCard()) {
            ROOT_PATH = getExternalROOTPath();
        } else {
            ROOT_PATH = getInternalROOTPath();
        }
        File dir = new File(ROOT_PATH);
        if (!dir.exists()){
            if(!dir.mkdirs()){
                return;
            }
        }
    }
}
