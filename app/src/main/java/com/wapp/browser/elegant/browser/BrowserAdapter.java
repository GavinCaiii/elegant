/*
 *   文件名:  CustomBrowserAdapter.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-09-06
 */

package com.wapp.browser.elegant.browser;

import android.app.Activity;
import android.content.Intent;

import com.wapp.browser.elegant.browser.paramters.BrowserParams;
import com.wapp.browser.elegant.browser.paramters.ButtonBean;

/**
 * @author Guangzhao Cai
 * @className: CustomBrowserAdapter
 * @classDescription:
 * @createTime: 2018-09-06
 */
public abstract class BrowserAdapter {

    /**
     * 当一个新的窗口创建时，将这个窗口的Activity实例传递给开发者，以便开发者统一管理应用的Activity
     * @param activity
     * @return
     */
    abstract public void onWindowCreate(Activity activity);

    /**
     * 映射Activity Resume
     * @param activity
     */
    abstract public void onWindowResume(Activity activity);

    /**
     * 映射Activity stop
     * @param activity
     */
    abstract public void onWindowPaused(Activity activity);

    /**
     * 当一个窗口被销毁时，调用这个接口，以便开发者统一管理Activity
     * @param activity
     * @return
     */
    abstract public void onWindowClosed(Activity activity);

    /**
     * 映射Activity result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    abstract public void onWindowResult(int requestCode, int resultCode, Intent data);

    /**
     * 实现大图显示
     * @param url 大图地址
     * @return
     */
    abstract public boolean showImage(String url);

    /**
     * 显示对话框
     * @param buttonBean 按钮对象
     * @param cb 回调，将用户点击的button index回传
     * @return
     */
    abstract public boolean showAlert(
            ButtonBean buttonBean,
            JsParamsCallback cb
    );

    /**
     * 实现本地的分享支持
     * @param type 分享类型
     * @param callback 分享结果回调
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean doShare(String type, JsParamsCallback callback);

    /**
     * 获取用户信息
     * @author swallow
     * @param callback 获取用户信息结果回调
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean getUserInfo(String method, JsParamsCallback callback);

    /**
     * 踢下线
     * @author swallow
     * @param message 踢下线的信息
     * @return false = 未处理， true = 已处理
     */
    abstract public boolean kickOut(String message);

    /**
     * 发起一个请求
     * @param path 请求路径
     * @param paramJson 请求参数
     * @param cb 请求回调
     * @return
     */
    abstract public boolean request(String path, String paramJson, String method, JsParamsCallback cb);

    /**
     * 返回到任意页面
     * @param url - 页面跳转url
     * @return
     */
    abstract public boolean goBack(String url);

    /**
     * 关闭页面
     * @return
     */
    abstract public boolean close();

    /**
     * 打开外部app
     * @param appUrl - app路径
     * @param downloadUrl - app下载链接
     * @return
     */
    abstract public boolean openApp(String appUrl, String downloadUrl);

    /**
     * 获取上一级或者多级页面的DeepLink URL
     * @param index
     * @param method
     * @param callback
     * @return
     */
    abstract public boolean getDeepLinkUrl(int index, String method, JsParamsCallback callback);


    abstract public boolean open(BrowserParams browserParams);

    /**
     * 未支持的uri通过该方法分发给开发者进行处理
     * @param url 未支持的url
     * @return
     */
    abstract public boolean interceptUrl(String url, AfterHandle cb);

    /**
     * 扫码
     * @param type - 0: OBD条形码
     * @param timeout - 超时倒计时。小于或等于0，则不进行倒计时
     * @param cb
     * @return
     */
    abstract public boolean scanCode(int type, int timeout, String method, JsParamsCallback cb);

    /**
     * 支付
     * @param businessCode 订单id
     * @param source 服务的业务代号
     * @return
     */
    abstract public boolean pay(String businessCode, String source);

    /**
     * 对话框回调
     * @author swallow
     * @since 2016.5.20
     */
    public interface AlertCallback {
        void callback(int index);
    }

    /**
     * 分享回调
     * @author swallow
     * @since 2016/4/22
     */
    public interface ShareCallback {
        void callback();
    }

    /**
     * 获取用户信息回调
     * @author: swallow
     * @since 2016/4/22
     */
    public interface UserInfoCallback {
        void callback(String json);
    }

    public interface ScanCodeCallback {
        /**
         * 扫描结果回调
         * @param params
         */
        void callBack(String params);
    }

    /**
     * 请求回调，将请求的返回解析成一个json字符串返回
     * @author swallow
     * @since 2016.4.27
     */
    public interface RequestCallback {
        void callback(String resJson);
    }

    /**
     * 开发者处理完之后重新回传给WappBrowser处理
     * @author swallow
     * @since 2016.5.17
     */
    public interface AfterHandle {
        void onSuccess(String url);
        void onFailure(String message);
    }

    public interface JsParamsCallback {
        void callback(String method, String params);
    }
}
