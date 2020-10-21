package com.lqcode.adjump;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.tools.ValueTools;


public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        CacheTools.getInstance().setContext(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        SwitchCompat jumpToastSwitch = findViewById(R.id.jump_toast_switch);
        SwitchCompat weixinAutoLoginSwitch = findViewById(R.id.weixin_auto_login_switch);
        jumpToastSwitch.setChecked(ValueTools.build().getInt("jump_toast_switch") > 0);
        weixinAutoLoginSwitch.setChecked(ValueTools.build().getInt("weixin_auto_login_switch") > 0);
        jumpToastSwitch.setOnCheckedChangeListener((compoundButton, b) -> ValueTools.build().putInt("jump_toast_switch", b ? 1 : 0));
        weixinAutoLoginSwitch.setOnCheckedChangeListener((compoundButton, b) -> ValueTools.build().putInt("weixin_auto_login_switch", b ? 1 : 0));
    }
}
