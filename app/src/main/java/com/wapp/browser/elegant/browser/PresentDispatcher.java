package com.wapp.browser.elegant.browser;

import com.wapp.browser.elegant.browser.paramters.BrowserParams;
import com.wapp.browser.elegant.browser.paramters.ButtonBean;
import com.wapp.browser.elegant.utils.JsonUtil;
import com.wapp.browser.elegant.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Guangzhao Cai
 * @className: PresentDispatcher
 * @classDescription: 业务分发器
 * @createTime: 2018-09-05
 */
public class PresentDispatcher {

    private final String SHOW_ALERT = "alert_widget_native";
    private final String SHOW_IMG = "img_widget_native";
    private final String WX_SHARE = "wx_share_function_native";
    private final String USER_INFO = "user_info_function_native";
    private final String LOGOUT = "logout_function_native";
    private final String KICK_OUT = "kickout_function_native";
    private final String REQUEST = "request_function_native";
    private final String OPEN = "open_control_native";
    private final String CLOSE = "close_control_native";
    private final String GO_BACK = "back_control_native";
    private final String GO_HOME = "home_control_native";
    private final String OPEN_MAP = "map_control_native";
    private final String OPEN_APP = "app_control_native";
    private final String SCAN_CODE = "scan_function_native";
    private final String PAY = "pay_function_native";
    private final String OPEN_EXTERNAL_APP = "external_control_native";
    private final String GET_DEEP_LINK_URL = "get_previous_control_native";

    private static PresentDispatcher mInstance;

    private CustomWebView mCustomWebView;

    private BrowserAdapter mBrowserAdapter = BrowserManager.getInstance().getBrowserAdapter();

    private static final String JAVASCRIPT_PREFIX = "javascript:";

    public static PresentDispatcher getInstance() {
        if (mInstance == null) {
            synchronized (PresentDispatcher.class) {
                if (mInstance == null) {
                    mInstance = new PresentDispatcher();
                }
            }
        }
        return mInstance;
    }

    public void setWebView(CustomWebView webView) {
        mCustomWebView = webView;
    }

    private PresentDispatcher() {}

    /**
     * 分发业务
     * @param path
     * @param params
     */
    public void dispatch(CustomWebView customWebView, String path, JSONObject params, int wappType) {
        if (mBrowserAdapter == null && params == null) {
            return;
        }
        mCustomWebView = customWebView;
        switch (path) {
            // 扫码
            case SCAN_CODE:
                scanCode(params);
                break;
            // 获取用户信息
            case USER_INFO:
                getUserInfo(params);
                break;
            // 支付
            case PAY:
                pay(params);
                break;
            // 踢下线
            case KICK_OUT:
                kickOut(params);
                break;
            // 分享
            case WX_SHARE:
                share(params);
                break;
            // 对话框
            case SHOW_ALERT:
                showAlertDialog(params);
                break;
            // 放大图片
            case SHOW_IMG:
                showImage(params);
                break;
            // 返回
            case GO_BACK:
                goBack(params);
                break;
            // 打开外部app
            case OPEN_EXTERNAL_APP:
                openExternalApp(params);
                break;
            // 返回上N级的Url
            case GET_DEEP_LINK_URL:
                getDeepLinkUrl(params);
                break;
            // 打开
            case OPEN:
                open(wappType, params);
                break;
            // 请求数据
            case REQUEST:
                request(params);
                break;
            default:
                break;
        }
    }

    /**
     * 扫码
     * @param params
     */
    private void scanCode(JSONObject params) {
        int businessCode = getInt(params, "business_code");
        int timeout = getInt(params, "timeout");
        String callback = getString(params, "callback");
        mBrowserAdapter.scanCode(businessCode, timeout, callback, mJsParamsCallback);

    }

    /**
     * 获取用户信息
     * @param params
     */
    private void getUserInfo(JSONObject params) {
        String callback = getString(params, "callback");
        mBrowserAdapter.getUserInfo(callback, mJsParamsCallback);
    }

    /**
     * 支付
     * @param params
     */
    private void pay(JSONObject params) {
        String orderId = getString(params, "orderId");
        String source = getString(params, "source");
        mBrowserAdapter.pay(orderId, source);
    }

    /**
     * 踢下线
     * @param params
     */
    private void kickOut(JSONObject params) {
        mBrowserAdapter.kickOut(params.toString());
    }

    /**
     * 分享
     * @param params
     */
    private void share(JSONObject params) {
        String shareType = getString(params, "share_type");
        mBrowserAdapter.doShare(shareType, mJsParamsCallback);
    }

    /**
     * 显示对话框
     * @param params
     */
    private void showAlertDialog(JSONObject params) {
        ButtonBean buttonBean = JsonUtil.fromJson(params.toString(), ButtonBean.class);
        mBrowserAdapter.showAlert(buttonBean, mJsParamsCallback);
    }

    /**
     * 图片放大
     * @param params
     */
    private void showImage(JSONObject params) {
        String imageUrl = getString(params, "img_url");
        mBrowserAdapter.showImage(imageUrl);
    }

    /**
     * 返回页面
     * @param params
     */
    private void goBack(JSONObject params) {
        String url = getString(params, "jump_url");
        mBrowserAdapter.goBack(url);
    }

    /**
     * 打开外部app
     * @param params
     */
    private void openExternalApp(JSONObject params) {
        String url = getString(params, "app_scheme");
        String downloadUrl = getString(params, "download_url");
        mBrowserAdapter.openApp(url, downloadUrl);
    }

    /**
     * 获取上一级或者多级页面的DeepLink URL
     * @param params
     */
    private void getDeepLinkUrl(JSONObject params) {
        int index = getInt(params, "index");
        String method = getString(params, "callback");
        mBrowserAdapter.getDeepLinkUrl(index, method, mJsParamsCallback);
    }

    /**
     * 打开新的页面
     * @param params
     */
    private void open(int wappType, JSONObject params) {
        BrowserParams browserParams = JsonUtil.fromJson(params.toString(), BrowserParams.class);
        if (wappType == BrowserParams.BROWSER_TYPE_EXTERNAL) {
            if (browserParams == null) {
                return;
            }
            switch (browserParams.getBrowserType()) {
                case BrowserParams.BROWSER_TYPE_APP:
                    ToastUtil.toastShow("Can not open <AppPage> in a <ExternalPage>");
                    break;
                case BrowserParams.BROWSER_TYPE_WEB_APP:
                    ToastUtil.toastShow("Can not open <WebAppPage> in a <ExternalPage>");
                    break;
            }
        }
        mBrowserAdapter.open(browserParams);
    }

    /**
     * 请求数据
     * @param params
     */
    private void request(JSONObject params) {
        String requestParams = getString(params, "request_params");
        String requestPath = getString(params, "request_path");
        String method = getString(params, "callback");
        mBrowserAdapter.request(requestPath, requestParams, method, mJsParamsCallback);
    }

    /**
     * js参数回调
     */
    private BrowserAdapter.JsParamsCallback mJsParamsCallback = new BrowserAdapter.JsParamsCallback() {
        @Override
        public void callback(String method, String params) {
            method = method.replace("params", "'" + params + "'");
            mCustomWebView.callJsMethod(method);
        }
    };

    /**
     * 获取字符串
     * @param params
     * @param key
     * @return
     */
    private String getString(JSONObject params, String key) {
        String value = null;
        if (params == null || params.isNull(key)) {
            return null;
        }
        try {
            value = params.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取int
     * @param params
     * @param key
     * @return
     */
    private int getInt(JSONObject params, String key) {
        int value = -1;
        if (params == null || params.isNull(key)) {
            return value;
        }
        try {
            value = params.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }
}
