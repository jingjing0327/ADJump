package com.lqcode.adjump.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.room.util.StringUtil;

import com.lqcode.adjump.R;
import com.lqcode.adjump.entity.PayEntity;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.frame.net.ApiController;
import com.lqcode.adjump.tools.Tools;

import retrofit2.Call;


public class NewUserPayActivity extends BaseActivity {
    EditText onePhone;
    private static final String TAG = NewUserPayActivity.class.getName();
    private RadioButton zhifubaoRB;
    private RadioButton weixinRB;

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

        zhifubaoRB = findViewById(R.id.zhifubao);
        weixinRB = findViewById(R.id.weixin);


        findViewById(R.id.zhifubao_layout).setOnClickListener(view -> radioGroup.check(R.id.zhifubao));

        findViewById(R.id.weixin_layout).setOnClickListener(view -> radioGroup.check(R.id.weixin));

        Button nowPay = findViewById(R.id.now_pay);

        nowPay.setOnClickListener(view -> {
            if (onePhone.getText().length() <= 0) {
                onePhone.setError("不能为空");
                Tools.editFocus(onePhone);
                return;
            }
            if (twoPhone.getText().length() <= 0) {
                twoPhone.setError("不能为空");
                Tools.editFocus(twoPhone);
                return;
            }
            if (onePhone.getText().length() != 11) {
                onePhone.setError("手机号不正确");
                Tools.editFocus(onePhone);
                return;
            }
            if (twoPhone.getText().length() != 11) {
                twoPhone.setError("手机号不正确");
                Tools.editFocus(twoPhone);
                return;
            }
            if (!onePhone.getText().toString().equals(twoPhone.getText().toString())) {
                twoPhone.setError("两次填写的手机号不一样！");
                Tools.editFocus(onePhone);
                return;
            }

            if (onePhone.getText().toString().equals(twoPhone.getText().toString())) {
                getPayInfo();
            }
        });
        String price = CacheTools.getInstance().getSundryConfig().get("price");
        if (price != null && price.length() > 0)
            nowPay.setText(price);
    }


    /**
     *
     */
    private void getPayInfo() {
        boolean isZhifubao = zhifubaoRB.isChecked();
        new Thread(() -> {
            try {
                String phone = onePhone.getText().toString();
                Call<Result<PayEntity>> payCall = ApiController.getService().payInfo(phone, isZhifubao ? "alipay" : "wechat");
                retrofit2.Response<Result<PayEntity>> payResponse = payCall.execute();
                Result<PayEntity> result = payResponse.body();
                if (result == null) return;
                if (result.getStatus() == 200) {
                    String codeUrl = result.getData().getCodeUrl();
                    Intent intent;
                    if (isZhifubao)
                        intent = new Intent(NewUserPayActivity.this, AlipayActivity.class);
                    else
                        intent = new Intent(NewUserPayActivity.this, WechatPayActivity.class);
                    intent.putExtra("url", codeUrl);
                    intent.putExtra("phone", phone);
                    startActivityForResult(intent, 200);
                } else {
                    XController.getInstance().toastShow(result.getMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 0x00327) {
            NewUserPayActivity.this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
