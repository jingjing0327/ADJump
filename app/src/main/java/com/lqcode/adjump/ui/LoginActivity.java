package com.lqcode.adjump.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.lqcode.adjump.R;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends BaseActivity {

    private Handler mHandler = new Handler();
    private EventHandler eh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MobSDK.submitPolicyGrantResult(true, null);
        xx();
        EditText phoneNumber = findViewById(R.id.phone_number);
        Button sendMSM = findViewById(R.id.send_msm);
        sendMSM.setOnClickListener(view -> {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SMSSDK.getVerificationCode("86", phoneNumber.getText().toString());
                        }
                    }).start();
                }
        );
    }

    private void xx() {
        try {
            eh = new EventHandler() {
                @Override
                public void afterEvent(int event, int result, Object data) {
                    Message msg = new Message();
                    msg.arg1 = event;
                    msg.arg2 = result;
                    msg.obj = data;
                    mHandler.sendMessage(msg);
                }
            };
            //注册一个事件回调监听，用于处理SMSSDK接口请求的结果
            SMSSDK.registerEventHandler(eh);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}
