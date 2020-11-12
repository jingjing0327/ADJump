package com.lqcode.adjump.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.lqcode.adjump.R;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.frame.net.ApiController;
import com.lqcode.adjump.tools.Tools;

import java.io.IOException;

import retrofit2.Response;

public class KeyVipActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("激活码");

        EditText key_et = findViewById(R.id.edit_key);
        EditText phone_et = findViewById(R.id.edit_phone);


        findViewById(R.id.button2).setOnClickListener(view -> {
            if (key_et.getText().toString().length() == 0) {
                Tools.editFocus(key_et);
                key_et.setError("激活码不能为空！");
                return;
            }
            if (phone_et.getText().toString().length() != 11) {
                Tools.editFocus(phone_et);
                phone_et.setError("手机号错误！");
                return;
            }
            new Thread(() -> {
                try {
                    Response<Result<Integer>> resultResponse = ApiController.getService().activationKey(phone_et.getText().toString(), key_et.getText().toString()).execute();
                    Result<Integer> result = resultResponse.body();
                    assert result != null;
                    if (result.getStatus() == 200) {
                        if (result.getData() > 0) {
                            XController.getInstance().toastShow("激活成功，请使用“老用户激活”登录设备！");
                            finish();
                        }
                    } else {
                        XController.getInstance().toastShow(result.getMsg() + "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}
