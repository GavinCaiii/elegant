package com.wapp.browser.elegant.browser;

import android.app.Activity;
import android.content.Intent;

import com.wapp.browser.elegant.browser.paramters.BrowserParams;
import com.wapp.browser.elegant.browser.paramters.ButtonBean;

/**
 * @author Guangzhao Cai
 * @className:
 * @classDescription:
 * @createTime: 2018-10-26
 */
public class CustomBrowserAdapter extends BrowserAdapter {
    @Override
    public void onWindowCreate(Activity activity) {

    }

    @Override
    public void onWindowResume(Activity activity) {

    }

    @Override
    public void onWindowPaused(Activity activity) {

    }

    @Override
    public void onWindowClosed(Activity activity) {

    }

    @Override
    public void onWindowResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean showImage(String url) {
        return false;
    }

    @Override
    public boolean showAlert(ButtonBean buttonBean, JsParamsCallback cb) {
        return false;
    }

    @Override
    public boolean doShare(String type, JsParamsCallback callback) {
        return false;
    }

    @Override
    public boolean getUserInfo(String method, JsParamsCallback callback) {
        return false;
    }

    @Override
    public boolean kickOut(String message) {
        return false;
    }

    @Override
    public boolean request(String path, String paramJson, String method, JsParamsCallback cb) {
        return false;
    }

    @Override
    public boolean goBack(String url) {
        return false;
    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public boolean openApp(String appUrl, String downloadUrl) {
        return false;
    }

    @Override
    public boolean getDeepLinkUrl(int index, String method, JsParamsCallback callback) {
        return false;
    }

    @Override
    public boolean open(BrowserParams browserParams) {
        return false;
    }

    @Override
    public boolean interceptUrl(String url, AfterHandle cb) {
        return false;
    }

    @Override
    public boolean scanCode(int type, int timeout, String method, JsParamsCallback cb) {
        return false;
    }

    @Override
    public boolean pay(String businessCode, String source) {
        return false;
    }
}
