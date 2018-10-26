package com.wapp.browser.elegant.browser.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.wapp.browser.elegant.R;
import com.wapp.browser.elegant.browser.BrowserManager;
import com.wapp.browser.elegant.utils.StringUtils;
import com.wapp.browser.elegant.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.etActMainUrl)
    EditText mUrlEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * 打开url
     */
    private void openUrl() {
        String url = mUrlEt.getText().toString();
        if (!StringUtils.isUrlValid(url)) {
            ToastUtil.toastShow("请输入正确的链接。");
            return;
        }
        BrowserManager.getInstance().openUrl(this, url);
    }

    @OnClick({R.id.btnActMainDump})
    void onClick() {
        openUrl();
    }
}
