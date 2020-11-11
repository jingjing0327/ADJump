package com.lqcode.adjump.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.king.zxing.util.CodeUtils;
import com.lqcode.adjump.R;

public class WechatPayActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_pay);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("微信支付");
        String url = getIntent().getStringExtra("url");
        ImageView qrcode = findViewById(R.id.wechat_qrcode);
        Bitmap logo = ((BitmapDrawable)getResources().getDrawable(R.mipmap.weixin)).getBitmap();//将资源文件转化为bitmap

        Bitmap wechatPayQRcode= CodeUtils.createQRCode(url,600,logo);
        qrcode.setImageBitmap(wechatPayQRcode);

    }

}
