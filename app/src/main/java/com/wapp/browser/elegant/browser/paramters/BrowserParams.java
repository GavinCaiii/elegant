/*
 *   文件名:  BrowserParams.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-09-05
 */

/*
 *   文件名:  BrowserParams.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-08-29
 */

package com.wapp.browser.elegant.browser.paramters;

import com.google.gson.annotations.SerializedName;
import com.wapp.browser.elegant.utils.JsonUtil;

import java.io.Serializable;
import java.util.List;

/**
 * @author Guangzhao Cai
 * @className: BrowserParams
 * @classDescription:
 * @createTime: 2018-08-29
 */
public class BrowserParams implements Serializable {

    private static final long serialVersionUID = -3919101492161130505L;

    // 外链型 - 只支持跳转H5，不支持跳转原生页面
    public static final int BROWSER_TYPE_EXTERNAL = 0;
    // WebApp型 - 支持跳转H5和原生页面
    public static final int BROWSER_TYPE_WEB_APP = 1;
    // 原型app
    public static final int BROWSER_TYPE_APP = 2;
    // 原生型 - 只支持跳转原生页面，不支持跳转H5
    public static final int BROWSER_TYPE_NATIVE = 3;

    public static final String YM_DL_ACTION = "cn.ecparck.depplinking";

    /**
     * 拦截参数类型
     */
    @SerializedName("intercept_type")
    private int mInterceptType;
    /**
     * 浏览器支持的类型
     */
    @SerializedName("wapp_type")
    private int mBrowserType;
    /**
     * 标题
     */
    @SerializedName("wapp_title")
    private String mBrowserTitle;
    /**
     * 浏览器加载的链接
     */
    @SerializedName("wapp_url")
    private String mBrowserUrl;
    /**
     * 页面参数，json格式
     */
    @SerializedName("page_params")
    private String mPageParams;
    /**
     * 右上角按钮集合
     */
    @SerializedName("right_item")
    private List<RightItem> mRightItems;

    public int getInterceptType() {
        return mInterceptType;
    }

    public void setInterceptType(int interceptType) {
        mInterceptType = interceptType;
    }

    public int getBrowserType() {
        return mBrowserType;
    }

    public void setBrowserType(int browserType) {
        mBrowserType = browserType;
    }

    public String getBrowserTitle() {
        return mBrowserTitle;
    }

    public void setBrowserTitle(String browserTitle) {
        mBrowserTitle = browserTitle;
    }

    public String getBrowserUrl() {
        return mBrowserUrl;
    }

    public void setBrowserUrl(String browserUrl) {
        mBrowserUrl = browserUrl;
    }

    public List<RightItem> getRightItems() {
        return mRightItems;
    }

    public String getPageParams() {
        return mPageParams;
    }

    public void setPageParams(String pageParams) {
        mPageParams = pageParams;
    }

    public void setRightItems(List<RightItem> rightItems) {
        mRightItems = rightItems;
    }

    public class RightItem {
        /**
         * 文字按键标题
         */
        private String title;
        /**
         * 图片按钮
         */
        private String icon;
        /**
         * 点击事件（抛回给H5处理）
         */
        private String action;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    public String toJson() {
        return JsonUtil.toJson(this, BrowserParams.class);
    }
}
