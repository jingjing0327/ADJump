package com.lqcode.adjump;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.bugly.Bugly;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class MyApplication extends Application {
    private final String TAG = MyApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "a7c7d52afe", false);
        UMConfigure.init(this, "5f915ef44d7bf81a2ea90ffe", "Main", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        Fresco.initialize(this);

//        UMConfigure.getOaid(getApplicationContext(), new OnGetOaidListener() {
//            @Override
//            public void onGetOaid(String s) {
//                Log.d(TAG, "onGetOaid: ===>>>" + s);
//            }
//        });


    }
}
