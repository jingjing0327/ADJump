package com.lqcode.adjump.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lqcode.adjump.R;
import com.lqcode.adjump.entity.PayEntity;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;

import okhttp3.Request;
import okhttp3.Response;


public class NewUserPayActivity extends BaseActivity {
    EditText onePhone;
    private static final String TAG = NewUserPayActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_pay);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("新用户购买");

        onePhone = findViewById(R.id.editTextPhone);
        EditText twoPhone = findViewById(R.id.editTextPhone2);
//        findViewById(R.id.button5);

        RadioGroup radioGroup = findViewById(R.id.pay_radio_group);

        RadioButton zhifubaoRB = findViewById(R.id.zhifubao);
        RadioButton weixinRB = findViewById(R.id.weixin);


        findViewById(R.id.zhifubao_layout).setOnClickListener(view -> radioGroup.check(R.id.zhifubao));

        findViewById(R.id.weixin_layout).setOnClickListener(view -> radioGroup.check(R.id.weixin));

        findViewById(R.id.now_pay).setOnClickListener(view -> {
            if (onePhone.getText().length() <= 0) {
                onePhone.setError("不能为空");
                return;
            }
            if (twoPhone.getText().length() <= 0) {
                twoPhone.setError("不能为空");
                return;
            }

            if (!onePhone.getText().toString().equals(twoPhone.getText().toString())) {
                twoPhone.setError("两次填写的手机号不一样！");
                return;
            }


            if (onePhone.getText().toString().equals(twoPhone.getText().toString())) {
                getPayInfo();
            }
        });
    }


    private void getPayInfo() {
        new Thread(() -> {
            try {
                String phone = onePhone.getText().toString();
                Request requestMd5 = new Request.Builder()
//                        .url("https://api.lqcode.cn/pay/native/" + phone + "/alipay")
                        .url("http://192.168.1.81:8082/pay/native/" + phone + "/alipay")
                        .build();
                Response payInfoResponse = CacheTools.getInstance().getClient().newCall(requestMd5).execute();
                String bodyMd5 = payInfoResponse.body().string();
                Result result = JSON.parseObject(bodyMd5, Result.class);
                JSONObject resultJson = JSON.parseObject(bodyMd5);
                int status = resultJson.getInteger("status");
                if (status == 200) {
                    Object object = result.getData();
                    PayEntity payEntity = JSON.parseObject(object.toString(), PayEntity.class);
                    String codeUrl = payEntity.getCodeUrl();
                    Intent intent = new Intent(NewUserPayActivity.this, AliPayActivity.class);
                    intent.putExtra("url", codeUrl);
                    startActivity(intent);
                } else {
                    XController.getInstance().toastShow(resultJson.getString("msg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }


}
