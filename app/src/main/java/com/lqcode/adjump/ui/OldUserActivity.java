package com.lqcode.adjump.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.alibaba.fastjson.JSON;
import com.lqcode.adjump.R;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.frame.net.ApiController;
import com.lqcode.adjump.tools.Tools;
import com.lqcode.adjump.tools.ValueTools;
import com.mob.MobSDK;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import retrofit2.Call;

public class OldUserActivity extends BaseActivity {

    private static final String TAG = OldUserActivity.class.getName();
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
                try {
                    if (data.toString().equals("false")) {
                        XController.getInstance().toastShow("发送成功");
                        countDown();
                    } else {
                        Log.d(TAG, "handleMessage: " + data.toString());
                        String xx = JSON.parseObject(((Throwable) data).getMessage()).getString("description");
                        XController.getInstance().toastShow(xx);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    ValueTools.build().putInt("vip", 1);
                    XController.getInstance().toastShow("欢迎回来，亲爱的VIP用户");
                    finish();
                } else {
                    XController.getInstance().toastShow(JSON.parseObject(((Throwable) data).getMessage()).getString("description"));
                }
            }
        }
    };
    private EventHandler eh;
    private Button sendMSM;
    private boolean isSend = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("老用户激活");

        MobSDK.submitPolicyGrantResult(true, null);
        xx();
        EditText phoneNumber = findViewById(R.id.phone_number);
        sendMSM = findViewById(R.id.send_msm);
        sendMSM.setOnClickListener(view -> {
            if (isSend) {
                String phone = phoneNumber.getText().toString();
                if (phone.length() != 11) {
                    phoneNumber.setError("手机号不正确");
                    Tools.editFocus(phoneNumber);
                    return;
                }
                sendMSM.setBackgroundColor(Color.parseColor("#C6C6C6"));
                sendMSM.setText("请求中...");
                new Thread(() -> {
                    try {
                        Call<Result<Object>> judgeVIPCall = ApiController.getService().judgeVIP(phone);
                        retrofit2.Response<Result<Object>> resultResponse = judgeVIPCall.execute();
                        Result<Object> resultVIP = resultResponse.body();

                        if (resultVIP == null) return;

                        if (resultVIP.getData() != null) {
                            SMSSDK.getVerificationCode("86", phoneNumber.getText().toString());
                        } else {
                            XController.getInstance().toastShow("无" + phone + "的任何支付信息！");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        EditText checkMsm = findViewById(R.id.check_msm);

        Button checkMSMButton = findViewById(R.id.check_msm_button);

        checkMSMButton.setOnClickListener(view -> {
            SMSSDK.submitVerificationCode("86", phoneNumber.getText().toString(), checkMsm.getText().toString());
        });
    }


    int count = 0;

    private void countDown() {
        XController.getInstance().getmHandler().postDelayed(() -> {
            ++count;
            sendMSM.setText(count + "s");
            sendMSM.setBackgroundColor(Color.parseColor("#C6C6C6"));
            isSend = false;
            if (count < 60) {
                countDown();
            } else {
                count = 0;
                isSend = true;
                sendMSM.setText("重新发送");
                sendMSM.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }, 1000);
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
