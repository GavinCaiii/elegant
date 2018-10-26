package com.wapp.browser.elegant.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.wapp.browser.elegant.AppContext;
import com.wapp.browser.elegant.R;

/**
 * @author : AIDAN SU
 * @className : ToastUtil.java
 * @classDescription : 提示工具类
 * @createTime : 2014-1-3
 */
public class ToastUtil {

    private static Toast mToast;

    /**
     * 提示：服务器繁忙，请稍后再试
     */
    public static void toastSystemError() {
        String msg = AppContext.getInstance().getResources().getString(R.string.toast_network_error);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(AppContext.getInstance(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * 提示：网络连接不可用，请稍后重试
     */
    public static void toastNetWorkError() {
        String msg = AppContext.getInstance().getResources().getString(R.string.toast_network_request_error);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(AppContext.getInstance(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * 提示信息的调用方法
     *
     * @param msg 提示信息
     */
    public static void toastShow(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(AppContext.getInstance(), msg, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    /**
     * 提示信息的调用方法
     *
     * @param msg 提示信息
     */
    public static void toastShow(int msg) {
        if (msg > 0) {
            if (mToast != null)
                mToast.cancel();
            mToast = Toast.makeText(AppContext.getInstance(), msg, Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

}
