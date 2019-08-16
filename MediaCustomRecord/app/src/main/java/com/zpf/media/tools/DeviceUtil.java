package com.zpf.media.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;

import com.zpf.media.MyApplication;

import java.io.File;

/**
 * author:zpf
 * date:2019-08-16
 */
public class DeviceUtil {

    private static final String TAG = "DeviceUtil";
    /**
     * @param context 上下文环境变量
     * @return 每个应用程序的内存限制：以MB为单位
     * @brief 查询该设备上每个应用程序的内存限制
     */
    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /**
     * @brief 获取内部存放文件的目录
     * @detail /data/data/com.netposa.cyqz/files/
     */
    public static String getInternalFilesDir() {
        File dir = MyApplication.getInstance().getFilesDir();
        if (dir == null || !dir.exists()) {
            dir = new File(getInternalStorage(), "files");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        String filesDir = dir.getAbsolutePath();
        if (!filesDir.endsWith("/")) {
            filesDir += "/";
        }
        return filesDir;
    }

    /**
     * @return 内部存储区数据目录，目录以"/"结尾
     * @brief 获取应用程序内部存储区的数据目录
     */
    public static String getInternalStorage() {
        String internalPath = MyApplication.getInstance().getApplicationInfo().dataDir;
        if (internalPath == null || "".equals(internalPath.trim())) {
            internalPath = Constants.DEFAULT_INTERNAL_PATH;
        } else if (!internalPath.endsWith("/")) {
            internalPath += "/";
        }
        return internalPath;
    }

    /**
     * @return 应用程序外部存储区目录，目录以"/"结尾
     * @brief 获取应用程序外部存储区的目录
     */
    public static String getExternalStorage() {
        String externalPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (externalPath == null || "".equals(externalPath.trim()) || !FileUtil.fileExists(externalPath)) {
            externalPath = Constants.DEFAULT_EXTERNAL_PATH;
        } else if (!externalPath.endsWith("/")) {
            externalPath += "/";
        }
        return externalPath;
    }

    /**
     * @return int类型，设备操作系统的SDK版本
     * @brief 获取手机系统SDK版本
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }


    /**
     * @return true表示有，false表示无
     * @brief 判断手机是否有SD卡
     */
    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取文件路径空间大小
     *
     * @param path
     */
    public static long getUsableSpace(File path) {
        try {
            final StatFs stats = new StatFs(path.getPath());
            return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean isPathHasSpace(String path) {
        long freeeSize = getUsableSpace(new File(path));
        if (freeeSize <= 0)
            return false;
        else
            return freeeSize >= 1024 * 1024;
    }

    /**
     * 添加到媒体库
     */
    public static void galleryAddMedia(Context context, String path) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
