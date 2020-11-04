package com.lqcode.adjump.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.lqcode.adjump.R;

public class VIPActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("购买VIP");
        findViewById(R.id.new_user_btn).setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, NewUserPayActivity.class)));
        findViewById(R.id.vip_user_btn).setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, OldUserActivity.class)));
        findViewById(R.id.key_vip).setOnClickListener(view -> startActivity(new Intent(VIPActivity.this, KeyVipActivity.class)));
    }
}
