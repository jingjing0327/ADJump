package com.lqcode.adjump.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.lqcode.adjump.R;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.frame.net.ApiController;

import cn.smssdk.SMSSDK;
import retrofit2.Call;

public class AlipayActivity extends BaseActivity {
    private WebView webView;
    private String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("支付宝支付");
        String url = getIntent().getStringExtra("url");
        phone = getIntent().getStringExtra("phone");
        alipayWebView(url);
//        findViewById(R.id.alipay_btn).setOnClickListener(view -> webView.reload());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Thread(() -> {
            try {
                Call<Result<Object>> judgeVIPCall = ApiController.getService().judgeVIP(phone);
                retrofit2.Response<Result<Object>> resultResponse = judgeVIPCall.execute();
                Result<Object> resultVIP = resultResponse.body();

                if (resultVIP == null) return;

                if (resultVIP.getData() != null) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AlipayActivity.this);
                        builder.setTitle("提示");
                        builder.setMessage(phone + "，您好，您已经开通VIP，请到“老用户激活”入口处登录！");
                        builder.setCancelable(false);
                        builder.setPositiveButton("确定", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            setResult(0x00327, new Intent());
                            AlipayActivity.this.finish();
                        }).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void alipayWebView(String url) {
        webView = findViewById(R.id.webview);
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
