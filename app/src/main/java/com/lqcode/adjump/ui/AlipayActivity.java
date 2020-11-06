package com.lqcode.adjump.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.lqcode.adjump.R;

public class AlipayActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipy);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("支付宝支付");
        String url = getIntent().getStringExtra("url");
        alipayWebView(url);
    }

    private void alipayWebView(String url) {
        WebView webView = findViewById(R.id.webview);
        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // webSettings.setDatabaseEnabled(true);
        // 使用localStorage则必须打开
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (parseScheme(url)) {
                    try {
                        Intent intent;
                        intent = Intent.parseUri(url,
                                Intent.URI_INTENT_SCHEME);
                        intent.addCategory("android.intent.category.BROWSABLE");
                        intent.setComponent(null);
                        // intent.setSelector(null);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
    }

    /**
     * @param url
     * @return
     */
    private boolean parseScheme(String url) {
        if (url.contains("platformapi/startapp")) {
            return true;
        } else {
            return false;
        }
    }
}
