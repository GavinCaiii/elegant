/*
 *   文件名:  BrowserManager.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-08-29
 */

package com.wapp.browser.elegant.browser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.wapp.browser.elegant.browser.activity.BrowserActivity;
import com.wapp.browser.elegant.browser.paramters.BrowserParams;
import com.wapp.browser.elegant.utils.JsonUtil;

/**
 * @author Guangzhao Cai
 * @className: BrowserManager
 * @classDescription:
 * @createTime: 2018-08-29
 */
public class BrowserManager {

    public static final String DEEP_LINK_PARAM = "deeplink_param";

    private static BrowserManager sInstance;

    private BrowserManager() {}

    private BrowserAdapter mBrowserAdapter;

    public static BrowserManager getInstance() {
        if (sInstance == null) {
            synchronized (BrowserManager.class) {
                if (sInstance == null) {
                    sInstance = new BrowserManager();
                }
            }
        }
        return sInstance;
    }

    public void init(BrowserAdapter browserAdapter) {
        mBrowserAdapter = browserAdapter;
    }

    /**
     * 打开页面
     * @param context
     * @param params
     */
    public void openPage(Context context, BrowserParams params) {
        if (context == null || params == null || TextUtils.isEmpty(params.getBrowserUrl())) {
            return;
        }

        Intent intent = new Intent();
        if (params.getBrowserType() == BrowserParams.BROWSER_TYPE_APP) {
            // 打开原生页面
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse(params.getBrowserUrl()));
            intent.setAction(BrowserParams.YM_DL_ACTION);
            intent.putExtra(DEEP_LINK_PARAM, params.getPageParams());
        } else {
            intent.setClass(context, BrowserActivity.class);

            String paramsStr = JsonUtil.toJson(params, BrowserParams.class);

            if (TextUtils.isEmpty(paramsStr)) {
                return;
            }
            intent.putExtra(BrowserActivity.PARAMS, paramsStr);
            intent.setClass(context, BrowserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**打开Url
     *
     * @param context
     * @param url
     */
    public void openUrl(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        BrowserParams params = new BrowserParams();
        params.setBrowserUrl(url);
        String paramsStr = JsonUtil.toJson(params, BrowserParams.class);
        if (TextUtils.isEmpty(paramsStr)) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(BrowserActivity.PARAMS, paramsStr);
        intent.setClass(context, BrowserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取浏览器适配器
     * @return
     */
    public BrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }
}
