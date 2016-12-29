package com.yeepbank.android.activity.user;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.yeepbank.android.R;
import com.yeepbank.android.base.BaseActivity;
import com.yeepbank.android.utils.LoadDialog;

/**
 * Created by WW on 2015/11/30.
 */
public class TickRaidersActivity extends BaseActivity {
    private View navigationBar;
    private WebView webView;
    @Override
    protected void initView() {
        navigationBar = findViewById(R.id.navigation_bar);
        webView = (WebView) findViewById(R.id.webview);
    }

    @Override
    protected void fillData() {
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/raiders.html");
    }

    @Override
    protected int getLayoutResources() {
        return R.layout.tick_raiders;
    }

    @Override
    protected View getNavigationBar() {
        return navigationBar;
    }

    @Override
    public LoadDialog getLoadDialog() {
        return null;
    }
}
