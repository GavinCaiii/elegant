package com.wapp.browser.elegant;

import android.app.Application;

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
    }

    public static AppContext getInstance() {
        return sInstance;
    }


}
