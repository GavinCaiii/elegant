/*
 *   文件名:  JavascriptBridge.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-08-29
 */

package com.wapp.browser.elegant.browser;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.wapp.browser.elegant.browser.paramters.BrowserParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Guangzhao Cai
 * @className: JavascriptBridge
 * @classDescription:
 * @createTime: 2018-08-29
 */
public class JavascriptBridge {

    private String mPath;

    private JSONObject mParamsJson;

    private final String PATH = "path";
    private final String PARAMS = "params";

    private CustomWebView mCustomWebView;

    private BrowserParams mBrowserParams;

    public void setWebView(CustomWebView customWebView) {
        mCustomWebView = customWebView;
        if (mCustomWebView != null) {
            mBrowserParams = mCustomWebView.getBrowerParams();
        }
    }

    @JavascriptInterface
    public void execLocal(String json) {

        if (TextUtils.isEmpty(json)) {
            return;
        }

        try {

            JSONObject data = new JSONObject(json);
            if (!data.isNull(PATH)) {
                mPath = data.getString(PATH);
            }
            if (!data.isNull(PARAMS)) {
                String params = data.getString(PARAMS);
                mParamsJson = new JSONObject(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(mPath) || mParamsJson == null) {
            return;
        }

        System.out.println("dddddddddddddd mPath = " + mPath);
        System.out.println("dddddddddddddd mParams = " + mParamsJson.toString());

        int wappType = 0;
        if (mBrowserParams != null) {
            wappType = mBrowserParams.getBrowserType();
        }

        // 业务分发
        PresentDispatcher.getInstance().dispatch(mCustomWebView, mPath, mParamsJson, wappType);

    }

}
