/*
 * 文件名:  UrlUtil.java
 * 版   权:  广州亚美信息科技有限公司
 * 创建人:  liguoliang
 * 创建时间:2018-05-02
 */

package com.wapp.browser.elegant.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @className: UrlUtil
 * @classDescription:
 * @author: liguoliang
 * @createTime: 2018/5/2
 */
public class UrlUtil {
    public static String getHost(String strUrl) {
        if (TextUtils.isEmpty(strUrl)) {
            return null;
        }
        try {
            URL url = new URL(strUrl);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getScheme(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri uri = UrlUtil.parseUri(url);
        if (uri == null) {
            return null;
        }
        return uri.getScheme();
    }

    public static boolean isHttpUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String scheme = getScheme(url);
        if (TextUtils.isEmpty(scheme)) {
            return false;
        }
        if ("http".equals(scheme) || "https".equals(scheme)) {
            return true;
        }
        return false;
    }

    public static Uri parseUri(String strUrl) {
        if (TextUtils.isEmpty(strUrl)) {
            return null;
        }
        try {
            return Uri.parse(strUrl);
        } catch (Exception e) {

        }
        return null;
    }

    public static Uri fromFile(File file) {
        if (file == null) {
            return null;
        }
        try {
            return Uri.fromFile(file);
        } catch (Exception e) {

        }
        return null;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        if (context == null) {
            return null;
        }
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
