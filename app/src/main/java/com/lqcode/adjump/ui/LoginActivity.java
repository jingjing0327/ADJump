package com.lqcode.adjump.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.lqcode.adjump.R;
import com.lqcode.adjump.frame.XController;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getName();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: " + msg.arg1);
            Log.d(TAG, "handleMessage: " + msg.arg2);
            Log.d(TAG, "handleMessage: " + msg.obj);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                XController.getInstance().toastShow(JSON.parseObject(((Throwable) data).getMessage()).getString("description"));
            } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    XController.getInstance().toastShow("验证码输入正确");
                } else {
                    XController.getInstance().toastShow(JSON.parseObject(((Throwable) data).getMessage()).getString("description"));
                }
            }
        }
    };
    private EventHandler eh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MobSDK.submitPolicyGrantResult(true, null);
        xx();
        EditText phoneNumber = findViewById(R.id.phone_number);
        Button sendMSM = findViewById(R.id.send_msm);
        sendMSM.setOnClickListener(view -> new Thread(() -> SMSSDK.getVerificationCode("86", phoneNumber.getText().toString())).start());
        EditText checkMsm = findViewById(R.id.check_msm);
        findViewById(R.id.check_msm_button).setOnClickListener(view -> SMSSDK.submitVerificationCode("86", phoneNumber.getText().toString(), checkMsm.getText().toString()));
    }

    /**
     *
     */
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
