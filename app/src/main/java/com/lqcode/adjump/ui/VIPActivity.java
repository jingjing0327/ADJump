package com.lqcode.adjump.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.lqcode.adjump.R;
import com.lqcode.adjump.tools.ValueTools;


public class VIPActivity extends BaseActivity {
    private TextView appVIPTitle;
    Button newUserBtn;
    Button vipUserBtn;
    Button keyVipButton;
    Button vipLoginOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("VIP");
        newUserBtn = findViewById(R.id.new_user_btn);
        newUserBtn.setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, NewUserPayActivity.class)));

        vipUserBtn = findViewById(R.id.vip_user_btn);
        vipUserBtn.setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, OldUserActivity.class)));

        keyVipButton = findViewById(R.id.key_vip);
        keyVipButton.setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, KeyVipActivity.class)));

        appVIPTitle = findViewById(R.id.textView5);

        vipLoginOut = findViewById(R.id.vip_login_out);
        vipLoginOut.setOnClickListener(view -> {
            ValueTools.build().putInt("vip", 0);
            ValueTools.build().putInt("jump_toast_switch", 0);
            ValueTools.build().putInt("weixin_auto_login_switch", 0);
            vipState();
        });
    }


    /**
     *
     */
    private void vipState() {
        if (ValueTools.build().getInt("vip") == 1) {
            newUserBtn.setVisibility(View.GONE);
            vipUserBtn.setVisibility(View.GONE);
            keyVipButton.setVisibility(View.GONE);
            vipLoginOut.setVisibility(View.VISIBLE);
            appVIPTitle.setText("当前版本-VIP");
        } else {
            newUserBtn.setVisibility(View.VISIBLE);
            vipUserBtn.setVisibility(View.VISIBLE);
            keyVipButton.setVisibility(View.VISIBLE);
            vipLoginOut.setVisibility(View.GONE);
            appVIPTitle.setText("当前版本-免费版");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        vipState();
    }
}
