package com.lqcode.adjump;

import android.app.Application;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class MyApplication extends Application {
    private final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
//        UMConfigure.init(this, "5f8fadeac1122b44acfc389b", "first", UMConfigure.DEVICE_TYPE_PHONE, null);
//        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

    }
}
