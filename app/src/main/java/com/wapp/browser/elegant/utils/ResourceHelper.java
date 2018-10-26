/*
 * 文件名:  ResourceUtils.java
 * 版   权:  广州亚美信息科技有限公司
 * 创建人:  liguoliang
 * 创建时间:2018-01-10
 */

package com.wapp.browser.elegant.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.ImageView;

import com.wapp.browser.elegant.AppContext;

/**
 * @className: ResourceUtils
 * @classDescription:
 * @author: liguoliang
 * @createTime: 2018/1/11
 */
public class ResourceHelper {
    private static final int DEFAULT_IMAGE_COLOR = 0xFFD9D9D9;
    private static ResourceHelper sResourceHelper;
    private Context mApplicationContext;

    private ResourceHelper() {
        mApplicationContext = AppContext.getInstance().getApplicationContext();
    }

    public static ResourceHelper getInstance() {
        if (sResourceHelper == null) {
            synchronized (ResourceHelper.class) {
                if (sResourceHelper == null) {
                    sResourceHelper = new ResourceHelper();
                }
            }
        }

        return sResourceHelper;
    }


    public String getString(int resId) {
        if (mApplicationContext == null || resId == 0) {
            return "";
        }
        try {
            return mApplicationContext.getString(resId);
        } catch (Resources.NotFoundException e) {
        }
        return "";
    }

    public String[] getStringArray(int resId) {
        if (mApplicationContext == null || resId == 0) {
            return new String[0];
        }
        try {
            return mApplicationContext.getResources().getStringArray(resId);
        } catch (Resources.NotFoundException e) {
        }
        return new String[0];
    }

    public int getColor(@ColorRes int resId) {
        if (mApplicationContext == null || resId == 0) {
            return 0;
        }
        try {
            return mApplicationContext.getResources().getColor(resId);
        } catch (Resources.NotFoundException e) {
        }
        return 0;
    }

    public int getDimensionPixelSize(int resId) {
        if (mApplicationContext == null || resId == 0) {
            return 0;
        }
        return mApplicationContext.getResources().getDimensionPixelSize(resId);
    }

    /**
     * 设置图片资源，加入OOM保护
     * @param imageView
     * @param resId
     */
    public static void setImageResource(ImageView imageView, int resId) {
        if (imageView == null || resId == 0) {
            return;
        }
        try {
            imageView.setImageResource(resId);
        } catch (OutOfMemoryError e) {

        }
    }

    /**
     * 设置背景资源，加入OOM保护
     * @param view
     * @param resId
     */
    public static void setBackgroundResource(View view, int resId) {
        if (view == null || resId == 0) {
            return;
        }
        try {
            view.setBackgroundResource(resId);
        } catch (OutOfMemoryError e) {

        }
    }

}
