package com.lqcode.adjump;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.listener.OnGetOaidListener;

public class MyApplication extends Application {
    private final String TAG = MyApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "a7c7d52afe", false);
        UMConfigure.init(this, "5f915ef44d7bf81a2ea90ffe", "Main", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        UMConfigure.getOaid(getApplicationContext(), new OnGetOaidListener() {
            @Override
            public void onGetOaid(String s) {
                Log.d(TAG, "onGetOaid: ===>>>"+s);
            }
        });


    }
}
