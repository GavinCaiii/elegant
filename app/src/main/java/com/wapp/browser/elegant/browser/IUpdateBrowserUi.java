/*
 *   文件名:  IUpdateBrowserUi.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-08-31
 */

package com.wapp.browser.elegant.browser;

import android.content.Context;

/**
 * @author Guangzhao Cai
 * @className: IUpdateBrowserUi
 * @classDescription:
 * @createTime: 2018-08-31
 */
public interface IUpdateBrowserUi {

    Context getContext();
    void setTitle(String title);
    void updateProgress(int progress);
    void showErrorView(boolean shouldShow);
    void openUrl(String url) throws Throwable;
    void goBack();
    void close();
}
