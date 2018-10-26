/*
 * 文件名:  FileUtil.java
 * 版   权:  广州亚美信息科技有限公司
 * 创建人:  liguoliang
 * 创建时间:2018-06-04
 */

package com.wapp.browser.elegant.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @className: FileUtil
 * @classDescription:
 * @author: liguoliang
 * @createTime: 2018-06-04
 */
public class FileUtil {
    /**
     * 创建文件夹
     *
     * @return File
     */
    public static boolean createDir(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File dir = new File(path);
        if (isExist(dir)) {
            return true;
        }
        try {
            return dir.mkdirs();
        } catch (Exception e) {

        }
        return false;
    }

    public static File createFile(String path, String fileName) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(fileName)) {
            return null;
        }
        if (!createDir(path)) {
            return null;
        }
        File file = new File(path, fileName);
        if (file.exists()) {
            return file;
        }
        try {
            if (file.createNewFile()) {
                return file;
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 读取文本文件
     *
     * @param fileName
     * @return
     */
    public static String read(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        try {
            File file = new File(fileName);
            FileInputStream in = new FileInputStream(file);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readInStream(InputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return null;
    }

    /**
     * 判断文件或者文件夹是否存在
     * @param file
     * @return
     */
    public static boolean isExist(File file) {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    public static boolean isExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return isExist(file);
    }

    public static boolean isExist(String filePath, String fileName) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(fileName)) {
            return false;
        }
        File file = new File(filePath, fileName);
        return isExist(file);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return 删除成功返回true，否则返回false
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }
        try {
            if (!dir.exists()) {
                return false;
            }
            if (dir.isDirectory()) {
                String[] children = dir.list();
                if (children == null || children.length < 1) {
                    return true;
                }
                // 递归删除目录中的子目录下
                for (String aChildren : children) {
                    boolean success = deleteDir(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
            }
            // 目录此时为空，可以删除
            return dir.delete();
        } catch (Exception e) {

        }
       return false;
    }

    public static long getFileLength(File file) {
        if (file == null || !file.exists()) {
            return -1;
        }
        try {
            return file.length();
        } catch (Exception e) {

        }
        return -1;
    }
}
