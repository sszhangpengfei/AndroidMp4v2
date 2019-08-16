package com.zpf.media.tools;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author:zpf
 * date:2019-08-16
 */
public class FileUtil {

    /**
     * @param filePath 文件路径
     * @brief 删除文件 - 只会删除文件或者空目录文件 - 如果参数所代表的路径是目录并目录下有文件，则删除失败
     */
    public static boolean deleteFile(@NonNull String filePath) {

        File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                // 若FileAPI删除失败，用命令删除
                try {
                    Runtime.getRuntime().exec("rm -r -f " + filePath);
                } catch (IOException e) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param filePath 目录名称
     * @brief 判断目录是否存在
     */
    public static boolean fileExists(String filePath) {
        // if filePath is null, return false, to resolve nullpointer of File Class Constructor
        if (filePath == null) return false;

        File file = new File(filePath);
        return file.exists();
    }

    /**
     * @param path 目录名称
     * @return 创建成功：true，创建失败：false
     * @brief 创建目录 - 会将必要的父级目录一并创建 - 如果该目录已经存在，则直接返回true
     */
    public static boolean mkdirs(String path) {
        if (path == null) return false;

        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    public static byte[] fileToBytes(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        byte[] data = null;
        BufferedInputStream bis = null;
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            bis = new BufferedInputStream(fileStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream((int) file.length());
            byte[] temp = new byte[1024];
            int len = 0;
            do {
                len = bis.read(temp);
                if (len > 0) {
                    baos.write(temp, 0, len);
                }
            } while (len > 0);
            data = baos.toByteArray();
        } catch (IOException e) {
            data = null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                    if(fileStream != null){
                        fileStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * @brief 将指定字节数组保存到文件
     */
    public static boolean saveFile(byte[] data, String targetFile) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            fos.write(data);
            fos.flush();
            return true;
        } catch (Exception e) {
            CLog.e(FileUtil.class.getName(),e.getMessage());
            e.printStackTrace();
        }  finally {
            if (fos != null) {
                fos.close();
            }
        }
        return false;
    }

    public static File rename(String filePath, String newName) {
        return rename(getFileByPath(filePath), newName);
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }



    /**
     * 重命名文件
     *
     * @param file    文件
     * @param newName 新名称
     * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
     */
    public static File rename(File file, String newName) {
        // 文件为空返回false
        if (file == null) return null;
        // 文件不存在返回false
        if (!file.exists()) return null;
        // 新的文件名为空返回false
        if (TextUtils.isEmpty(newName)) return null;
        // 如果文件名没有改变返回true
        if (newName.equals(file.getName())) return null;
        File newFile = new File(file.getParent() + File.separator + newName);
        if(!newFile.exists() && file.renameTo(newFile)){
            return newFile;
        }

        // 如果重命名的文件已存在返回false
        return null;
    }

}
