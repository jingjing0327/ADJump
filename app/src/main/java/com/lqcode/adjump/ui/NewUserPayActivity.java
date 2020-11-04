package com.lqcode.adjump.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.lqcode.adjump.R;

public class NewUserPayActivity extends BaseActivity {
    private static final String TAG = NewUserPayActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_pay);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新用户购买");

//        findViewById(R.id.editTextPhone);
//        findViewById(R.id.editTextPhone2);
//        findViewById(R.id.button5);

        RadioGroup radioGroup = findViewById(R.id.pay_radio_group);

        RadioButton zhifubaoRB = findViewById(R.id.zhifubao);
        RadioButton weixinRB = findViewById(R.id.weixin);


        findViewById(R.id.zhifubao_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.zhifubao);
            }
        });

        findViewById(R.id.weixin_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.check(R.id.weixin);
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {










            }
        });
    }
}
