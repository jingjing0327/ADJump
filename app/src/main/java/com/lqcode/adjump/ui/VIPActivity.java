package com.lqcode.adjump.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.lqcode.adjump.R;

public class VIPActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        findViewById(R.id.new_user_btn).setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, PayActivity.class)));
        findViewById(R.id.vip_user_btn).setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, LoginActivity.class)));
        findViewById(R.id.key_vip).setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, KeyVipActivity.class)));
    }
}
