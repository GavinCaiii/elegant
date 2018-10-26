package com.wapp.browser.elegant.browser.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wapp.browser.elegant.R;
import com.wapp.browser.elegant.browser.BrowserAdapter;
import com.wapp.browser.elegant.browser.BrowserManager;
import com.wapp.browser.elegant.browser.CustomWebView;
import com.wapp.browser.elegant.browser.IUpdateBrowserUi;
import com.wapp.browser.elegant.browser.paramters.BrowserParams;
import com.wapp.browser.elegant.utils.DisplayUtils;
import com.wapp.browser.elegant.utils.JsonUtil;
import com.wapp.browser.elegant.utils.imageloader.GlideImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Guangzhao Cai
 * @className: BrowserActivity
 * @classDescription:
 * @createTime: 2018-08-28
 */
public class BrowserActivity extends BaseActivity implements IUpdateBrowserUi {

    public static final String PARAMS = "params";

    private final int RIGHT_CLICK_REFRESH_TYPE = 1;
    private final int RIGHT_CLICK_EVENT_TYPE = 2;
    private final int RIGHT_CLICK_MORE_TYPE = 3;

    @BindView(R.id.tvBrowserActTitle)
    TextView mTitleTv;
    @BindView(R.id.ivBrowserActLeft)
    ImageView mLeftIb;
    @BindView(R.id.tvBrowserActRight)
    TextView mRightTv;
    @BindView(R.id.ivBrowserActRightFirst)
    ImageView mRightFirstIb;
    @BindView(R.id.ivBrowserActRightSecond)
    ImageView mRightSecondIb;
    @BindView(R.id.wvActBrowser)
    CustomWebView mWebView;
    @BindView(R.id.pbBrowserAct)
    ProgressBar mProgressBar;
    @BindView(R.id.rlActBrowserErr)
    RelativeLayout mErrorViewRl;
    @BindView(R.id.ivActBrowserErr)
    ImageView mErrorIv;


    private BrowserParams mBrowserParams;

    private int mRightItemClickType = 0;

    private List<BrowserParams.RightItem> mRightItems;

    private BrowserParams.RightItem mRightItem;

    private PopupWindow mPopupWindow;

    private final String ON_RESUME_EVENT = "wapp.utils.notification.view.will.appear()";
    private final String ON_STOP_EVENT = "wapp.utils.notification.view.did.disappear()";
    private final String ON_DESTROY_EVENT = "wapp.utils.notification.view.dealloc()";

    private BrowserAdapter mBrowserAdapter = BrowserManager.getInstance().getBrowserAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView.setBrowserUi(this);
        mWebView.setBrowserAdapter(mBrowserAdapter);

        //恢复到被回收前的页面
        if (savedInstanceState != null) {
            mBrowserParams = JsonUtil.fromJson(savedInstanceState.getString(PARAMS), BrowserParams.class);
        } else {
            mBrowserParams = getParams(getIntent());
        }

        if (mBrowserParams == null || TextUtils.isEmpty(mBrowserParams.getBrowserUrl())) {
            return;
        }

        initToolbar(mBrowserParams);

        try {
            openUrl(mBrowserParams.getBrowserUrl());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        mWebView.setBrowserParams(mBrowserParams);
        mBrowserAdapter.onWindowCreate(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_browser;
    }

    /**
     * 初始化toolbar
     * @param browserParams
     */
    private void initToolbar(BrowserParams browserParams) {
        switch (browserParams.getBrowserType()) {
            case BrowserParams.BROWSER_TYPE_EXTERNAL:
            case BrowserParams.BROWSER_TYPE_WEB_APP:
                mLeftIb.setImageDrawable(getResources().getDrawable(R.drawable.ic_navbar_close));
                break;
            case BrowserParams.BROWSER_TYPE_APP:
                mLeftIb.setImageDrawable(getResources().getDrawable(R.drawable.ic_navbar_back));
                break;
        }
        mRightItems = browserParams.getRightItems();
        if (mRightItems == null || mRightItems.isEmpty()) {
            mRightSecondIb.setImageDrawable(getResources().getDrawable(R.drawable.ic_navbar_refresh));
            mRightSecondIb.setVisibility(View.VISIBLE);
            mRightItemClickType = RIGHT_CLICK_REFRESH_TYPE;
            return;
        }
        try {
            // 只有一个按钮
            if (mRightItems.size() == 1) {
                mRightItem = mRightItems.get(0);
                String icon = mRightItems.get(0).getIcon();
                String title = mRightItems.get(0).getTitle();
                if (!TextUtils.isEmpty(icon)) {
                    mRightSecondIb.setVisibility(View.VISIBLE);
                    GlideImageLoader.create(mRightSecondIb).loadImage(icon);
                } else if (!TextUtils.isEmpty(title)) {
                    mRightTv.setVisibility(View.VISIBLE);
                    mRightTv.setText(title);
                }
                mRightItemClickType = RIGHT_CLICK_EVENT_TYPE;
                return;
            }
            // 两个按钮
            if (mRightItems.size() == 2) {
                mRightItem = mRightItems.get(1);
                GlideImageLoader.create(mRightFirstIb).loadImage(mRightItems.get(0).getIcon());
                GlideImageLoader.create(mRightSecondIb).loadImage(mRightItems.get(1).getIcon());
                mRightFirstIb.setVisibility(View.VISIBLE);
                mRightSecondIb.setVisibility(View.VISIBLE);
                mRightItemClickType = RIGHT_CLICK_EVENT_TYPE;
                return;
            }
            // 两个以上的按钮
            mRightSecondIb.setVisibility(View.VISIBLE);
            mRightSecondIb.setImageDrawable(getResources().getDrawable(R.drawable.ic_navbar_more));
            mRightItemClickType = RIGHT_CLICK_MORE_TYPE;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mBrowserParams.setBrowserUrl(mWebView.getUrl());
        outState.putString(PARAMS, mBrowserParams.toJson());
        super.onSaveInstanceState(outState);
    }

    private BrowserParams getParams(Intent intent) {
        if (intent == null) {
            return null;
        }
        String strParams = intent.getStringExtra(PARAMS);
        if (TextUtils.isEmpty(strParams)){
            return null;
        }
        return JsonUtil.fromJson(strParams, BrowserParams.class);
    }

    @Override
    protected void onResume() {
        mWebView.callJsMethod(ON_RESUME_EVENT);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.callJsMethod(ON_STOP_EVENT);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.callJsMethod(ON_DESTROY_EVENT);
        mBrowserAdapter.onWindowClosed(this);
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void setTitle(String title) {
        if (mBrowserParams != null && !TextUtils.isEmpty(mBrowserParams.getBrowserTitle())) {
            mTitleTv.setText(mBrowserParams.getBrowserTitle());
        } else {
            mTitleTv.setText(title);
        }
    }

    @Override
    public void updateProgress(int progress) {
        //平滑的加载进度
        if (progress < 2) {
            mProgressBar.setProgress(2);
        } else if (progress > mProgressBar.getProgress()) {
            ObjectAnimator animation = ObjectAnimator.ofInt(mProgressBar, "progress", progress);
            int duration = (int) ((float) (progress - mProgressBar.getProgress()) / (float) 100 * 600);
            animation.setDuration(duration);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        } else {
            mProgressBar.setProgress(progress);
        }

        if (progress == 100) {
            //进度条优雅的退场
            ObjectAnimator outAni = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1, 0);
            outAni.setStartDelay(200);
            outAni.setDuration(200);
            outAni.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(View.GONE);
                    mProgressBar.setAlpha(1);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            outAni.start();
        } else {
            //进度条加载时始终使其可见
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showErrorView(boolean shouldShow) {
        if (shouldShow) {
            mErrorViewRl.setVisibility(View.VISIBLE);
        } else {
            mErrorViewRl.setVisibility(View.GONE);
        }
    }

    @Override
    public void openUrl(String url) throws Throwable {
        if (mWebView.interceptUrl(url)) {
            return;
        }
        mWebView.loadUrl(url);
    }

    @Override
    public void goBack() {

    }

    @Override
    public void close() {

    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            this.finish();
        }
    }

    /**
     * 点击事件
     * @param view
     */
    @OnClick({R.id.ivBrowserActLeft, R.id.ivBrowserActRightFirst, R.id.ivBrowserActRightSecond,
    R.id.ivActBrowserErr})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBrowserActLeft:
                if (mBrowserParams.getBrowserType() == BrowserParams.BROWSER_TYPE_APP) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.ivBrowserActRightFirst:
                mWebView.callJsMethod(mRightItems.get(0).getAction());
                break;
            case R.id.ivBrowserActRightSecond:
                onRightItemClickEvent(mRightItemClickType);
                break;
            case R.id.ivActBrowserErr:
                mWebView.reload();
                break;
        }
    }

    /**
     * 右边按钮点击事件
     * @param type
     */
    private void onRightItemClickEvent(int type) {
        switch (type) {
            // 刷新
            case RIGHT_CLICK_REFRESH_TYPE:
                mWebView.reload();
                break;
            // 事件
            case RIGHT_CLICK_EVENT_TYPE:
                mWebView.callJsMethod(mRightItem.getAction());
                break;
            // 更多
            case RIGHT_CLICK_MORE_TYPE:
                showMenu();
                break;
        }
    }

    /**
     * 显示菜单
     */
    private void showMenu() {
        if (mPopupWindow == null) {
            LinearLayout contentView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.toolbar_menu, new LinearLayout(this), false);
            for (int i = 0; i < mRightItems.size(); i++) {
                View view = View.inflate(this, R.layout.toolbar_menu_item, new LinearLayout(this));
                ImageView iconIv = view.findViewById(R.id.ivToolbarMenuItem);
                TextView titleTv = view.findViewById(R.id.tvToolbarMenuItem);
                LinearLayout menuItemLl = view.findViewById(R.id.llToolbarMenuItem);
                final int index = i;
                menuItemLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPopupWindow != null && mPopupWindow.isShowing()) {
                            mPopupWindow.dismiss();
                            mWebView.callJsMethod(mRightItems.get(index).getAction());
                        }
                    }
                });
                GlideImageLoader.create(iconIv).loadImage(mRightItems.get(i).getIcon());
                titleTv.setText(mRightItems.get(i).getTitle());
                contentView.addView(view);
            }
            mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(BrowserActivity.this, 1.0f);
                }
            });
        }
        if (!mPopupWindow.isShowing()) {
            backgroundAlpha(this, 0.4f);
            mPopupWindow.showAsDropDown(mRightSecondIb, DisplayUtils.dip2px(BrowserActivity.this, -125), 0);
        }
    }

    /**
     * 设置popupwindow外面背景透明度
     * @param bgalpha 透明度  0-1   0-透明   1-不透明
     */
    public void backgroundAlpha(Activity activity, float bgalpha) {
        if (activity == null) {
            return;
        }
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgalpha;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 获取webview实例
     * @return
     */
    public CustomWebView getWebView() {
        return this.mWebView;
    }
}
