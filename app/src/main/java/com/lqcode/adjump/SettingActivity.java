package com.lqcode.adjump;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lqcode.adjump.frame.CacheTools;


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

    }
}
