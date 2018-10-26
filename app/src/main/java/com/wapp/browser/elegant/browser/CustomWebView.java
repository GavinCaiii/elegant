/*
 *   文件名:  CustomWebView.java
 *   版   权:   广州亚美信息科技有限公司
 *   创建人 :  Caiii-PC
 *   创建时间:  2018-08-29
 */

package com.wapp.browser.elegant.browser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


import com.wapp.browser.elegant.BuildConfig;
import com.wapp.browser.elegant.R;
import com.wapp.browser.elegant.browser.paramters.BrowserParams;
import com.wapp.browser.elegant.manager.ThreadManager;
import com.wapp.browser.elegant.utils.FileUtil;
import com.wapp.browser.elegant.utils.UrlUtil;

import java.io.File;

/**
 * @author Guangzhao Cai
 * @className: CustomWebView
 * @classDescription:
 * @createTime: 2018-08-29
 */
public class CustomWebView extends WebView {

    private final String JAVASCRIPT_INTERFACE_NAME = "local";

    private final String JAVASCRIPT_PREFIX = "javascript:";

    /**
     * 支持H5调起文件选择器
     */
    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mUploadFiles;

    public static final int REQUEST_CODE_GET_LOCAL_FILE = 333;
    public static final int REQUEST_CODE_TAKE_PHOTO = 334;

    private String mPhotoPath;

    public static final String MIME_TYPE_IMAGE = "image/*";

    private boolean mErrorFlag;
    private boolean mIsReady = false;

    private IUpdateBrowserUi mBrowserUi;
    private BrowserAdapter mBrowserAdapter;
    private BrowserParams mBrowserParams;

    /**
     * js调用原生方法集合类
     */
    private JavascriptBridge mJavascriptBridge;

    public CustomWebView(Context context) {
        super(context);
        initWebView();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView();
    }

    /**
     * 设置更新UI接口
     * @param browserUi
     */
    public void setBrowserUi(IUpdateBrowserUi browserUi) {
        mBrowserUi = browserUi;
    }

    /**
     * 设置浏览器适配器
     * @param browserAdapter
     */
    public void setBrowserAdapter(BrowserAdapter browserAdapter) {
        mBrowserAdapter = browserAdapter;
    }

    public void setBrowserParams(BrowserParams params) {
        mBrowserParams = params;
    }

    public BrowserParams getBrowerParams() {
        return mBrowserParams;
    }

    /**
     * 初始化webView
     */
    private void initWebView() {
        setWebViewClient(new CustomWebViewClient());
        setWebChromeClient(new CustomWebChromeClient());
        WebSettings webSettings = getSettings();
        //启用数据库
        webSettings.setDatabaseEnabled(true);
        String dir = getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        webSettings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setDomStorageEnabled(true);
        // 支持js
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 无缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setLoadWithOverviewMode(true);
        // 5.0以上需要添加http/https混合加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setPluginState(WebSettings.PluginState.ON);
        // 设置H5字体不随系统字体的改变而改变
        webSettings.setTextZoom(100);
        String userAgent = webSettings.getUserAgentString() + " WappBrowser/"+ BuildConfig.VERSION_NAME;
        // 设置UserAgent
        webSettings.setUserAgentString(userAgent);
        // 自动播放音乐
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        mJavascriptBridge = new JavascriptBridge();
        mJavascriptBridge.setWebView(this);
        addJavascriptInterface(mJavascriptBridge, JAVASCRIPT_INTERFACE_NAME);
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("dddddddddddddddd shouldOverrideUrlLoading url = " + url);
            // 拦截URL
            if (interceptUrl(url)) {
                return true;
            }
            // 尝试deepLink跳转
            if (supportDeepLink(url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mErrorFlag = false;
            mIsReady = false;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return;
            }
            mErrorFlag = true;
//            loadUrl(mErrorPage);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
//                loadUrl(mErrorPage);
                mErrorFlag = true;
                return;
            }
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 忽略ssl错误，继续加载网页
            handler.proceed();
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mBrowserUi != null) {
                mBrowserUi.setTitle(title);
            }
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mBrowserUi == null) {
                return;
            }
            // 更新网页加载进度
            mBrowserUi.updateProgress(newProgress);
            // 加载完后，如果出错就显示错误页
            if (newProgress >= 100) {
                mBrowserUi.showErrorView(mErrorFlag);
                if (!mErrorFlag && !mIsReady) {
                    mIsReady = true;
                }
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadFile = uploadMsg;
            openFileChoose(null);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadFile = uploadMsg;
            openFileChoose(acceptType);
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadFile = uploadMsg;
            openFileChoose(acceptType);
        }

        // For Android  >= 5.0
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            String acceptType = null;
            if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null && fileChooserParams.getAcceptTypes().length > 0) {
                acceptType = fileChooserParams.getAcceptTypes()[0];
            }
            mUploadFiles = filePathCallback;
            openFileChoose(acceptType);
            return true;
        }
    }

    /**
     * 打开文件选择器
     * @param acceptType
     */
    private void openFileChoose(String acceptType) {
        if (TextUtils.isEmpty(acceptType)) {
            startFileChoose();
            return;
        }
        if (MIME_TYPE_IMAGE.equals(acceptType)) {
            showChooseImageDialog();
        } else {
            startFileChoose();
        }
    }

    /**
     * 文件选择器
     * @return
     */
    private boolean startFileChoose() {
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }

        try {
            Intent intent = new Intent();
            intent.setType("*/*");
            /* 使用Intent.ACTION_GET_CONTENT这个Action */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            }
            activity.startActivityForResult(intent, REQUEST_CODE_GET_LOCAL_FILE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 显示拍照/图片选择对话框
     */
    private void showChooseImageDialog() {

        final Dialog dialog = new Dialog(getContext(), R.style.common_dialog);
//        final Dialog dialog = new Dialog(getContext(), R.style.common_dialog);
        dialog.setContentView(R.layout.dialog_camera_gallery_select);

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        WindowManager windowManager = activity.getWindowManager();
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.gravity = Gravity.BOTTOM;
                layoutParams.width = display.getWidth();
                dialog.getWindow().setAttributes(layoutParams);
            }
        }

        TextView tvCamera = dialog.findViewById(R.id.tvCamera);
        tvCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startCamera();
            }
        });

        TextView tvSelectFromAlbum = dialog.findViewById(R.id.tvSelectFromAlbum);
        tvSelectFromAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startImageChoose();
            }
        });

        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onActivityResult(REQUEST_CODE_GET_LOCAL_FILE, Activity.RESULT_CANCELED, null);
            }
        });
        dialog.show();

    }

    /**
     * 启动相机
     */
    private void startCamera() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        try {
            String cameraPath = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .getAbsolutePath()
                    + File.separator + "browser-photo";
            String fileName = System.currentTimeMillis() + ".jpg";
            File dir = new File(cameraPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mPhotoPath = cameraPath + File.separator + fileName;
            // 把文件地址转换成Uri格式
            Uri uri;
            uri = UrlUtil.fromFile(new File(mPhotoPath));
            if (uri == null) {
                uploadFileCancel();
                return;
            }
            // 设置系统相机拍摄照片完成后图片文件的存放地址
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动图片选择器
     * @return
     */
    private boolean startImageChoose() {
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }
        try {
            //调用相册
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(intent, REQUEST_CODE_GET_LOCAL_FILE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 选择文件回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_LOCAL_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    if (null != mUploadFile) {
                        if (data != null && data.getData() != null) {
                            Uri result = data.getData();
                            mUploadFile.onReceiveValue(result);
                        } else {
                            mUploadFile.onReceiveValue(null);
                        }
                        mUploadFile = null;
                    }
                    if (null != mUploadFiles) {
                        if (data != null && data.getData() != null) {
                            Uri result = data.getData();
                            mUploadFiles.onReceiveValue(new Uri[]{result});
                        } else {
                            mUploadFiles.onReceiveValue(null);
                        }
                        mUploadFiles = null;
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    uploadFileCancel();
                }
                break;
            case REQUEST_CODE_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    if (TextUtils.isEmpty(mPhotoPath) || !FileUtil.isExist(mPhotoPath)) {
                        uploadFileCancel();
                        return;
                    }
                    if (null != mUploadFile) {
                        Uri result = UrlUtil.fromFile(new File(mPhotoPath));
                        mUploadFile.onReceiveValue(result);
                        mPhotoPath = null;
                        mUploadFile = null;
                    }
                    if (null != mUploadFiles) {
                        Uri result = UrlUtil.fromFile(new File(mPhotoPath));
                        mUploadFiles.onReceiveValue(new Uri[]{result});
                        mPhotoPath = null;
                        mUploadFiles = null;
                    }
                } else if (resultCode == Activity.RESULT_CANCELED){
                    uploadFileCancel();
                    mPhotoPath = null;
                }
                break;
            default:
                break;
        }
    }

    private void uploadFileCancel() {
        if (null != mUploadFile) {
            mUploadFile.onReceiveValue(null);
            mUploadFile = null;
        }
        if (null != mUploadFiles) {
            mUploadFiles.onReceiveValue(null);
            mUploadFiles = null;
        }
    }

    private Activity getActivity() {
        Activity activity = null;
        if (getContext() instanceof Activity) {
            activity = (Activity) getContext();
        }
        return activity;
    }

    /**
     * 原生调用JS方法
     * @param method
     */
    public void callJsMethod(final String method) {
        System.out.println("ddddddddddddddd callJsMethod method = " + method);
        if (TextUtils.isEmpty(method)) {
            return;
        }

        ThreadManager.post(ThreadManager.THREAD_UI, new ThreadManager.RunnableEx() {
            @Override
            public void run() {
                // 4.3以下系统用loadUrl
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    loadUrl(JAVASCRIPT_PREFIX + method);
                } else {
                    evaluateJavascript(JAVASCRIPT_PREFIX + method, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                        }
                    });
                }
            }
        });
    }

    public boolean interceptUrl(String url) {
        return mBrowserAdapter.interceptUrl(url, mAfterHandle);
    }

    private BrowserAdapter.AfterHandle mAfterHandle = new BrowserAdapter.AfterHandle() {
        @Override
        public void onSuccess(String url) {
            loadUrl(url);
        }

        @Override
        public void onFailure(String message) {
            mBrowserUi.showErrorView(true);
        }
    };

    /**
     * 对一些特殊链接尝试进行DeepLink
     * @param url
     * @return
     */
    private boolean supportDeepLink(String url) {
        if (url.contains("http")) {
            return false;
        }
        try{
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
