package com.wapp.browser.elegant;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.utils.TbsLog;
import com.wapp.browser.elegant.browser.BrowserManager;
import com.wapp.browser.elegant.browser.CustomBrowserAdapter;


/**
 * @author Guangzhao Cai
 * @className:
 * @classDescription:
 * @createTime: 2018-10-26
 */
public class AppContext extends Application {

    public static AppContext sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        BrowserManager.getInstance().init(new CustomBrowserAdapter());

        initX5WebView();
    }

    public static AppContext getInstance() {
        return sInstance;
    }


    private void initX5WebView() {
        //如果没有这个内核，允许在WIFI情况下去下载内核
        QbSdk.setDownloadWithoutWifi(true);
//        TbsLog.setWriteLogJIT(true);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("AppContext", "x5内核初始化结果：" + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                Log.d("AppContext", "x5内核初始化完成");
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
