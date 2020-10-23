package com.lqcode.adjump;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class MyApplication extends Application {
    private final String TAG = MyApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
//        Bugly.init(getApplicationContext(), "a7c7d52afe", false);
//        UMConfigure.init(this, "5f915ef44d7bf81a2ea90ffe", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
//        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }
}
