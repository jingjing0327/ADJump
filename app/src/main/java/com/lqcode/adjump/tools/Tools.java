package com.lqcode.adjump.tools;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lqcode.adjump.entity.NetApps;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.entity.db.DBAppConfig;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import okhttp3.Request;
import okhttp3.Response;

public class Tools {
    private static final String TAG = Tools.class.getSimpleName();

    public static String getDeviceId() {
        String deviceId = ValueTools.build().getString("deviceId");
        if (deviceId == null) {
            deviceId = android.os.Build.BRAND + "-" + android.os.Build.MODEL + "-" + UUID.randomUUID().toString();
            File deviceFile = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.android.settings/lqcodeDevice.file");
            Log.d(TAG, "getDeviceId: " + deviceFile.getAbsolutePath());
            try {
                deviceFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            ValueTools.build().putString("deviceId", deviceId);
        }
        return deviceId;
    }

    public static void setCacheAppsConfig() {
        new Thread(() -> {
            List<DBAppConfig> dbAppConfigList = XController.getInstance().getDb().appConfigDao().getAll();
            Map<String, String> map = new HashMap<>();
            for (DBAppConfig config : dbAppConfigList) {
                map.put(config.getPackageActivity(), config.getButtonName());
            }
            CacheTools.getInstance().setApps(map);
            Log.d(TAG, "setCacheAppsConfig: " + CacheTools.getInstance().getApps().toString());
            getNewApps();
        }).start();
    }

    /**
     * 获取网络新的配置
     */
    public static void getNewApps() {
        try {
            //
            if (CacheTools.getInstance().getApps() == null || CacheTools.getInstance().getApps().size() <= 0)
                ValueTools.build().putString("appConfigMd5", UUID.randomUUID().toString());
            //
            Request requestMd5 = new Request.Builder()
                    .url("https://api.lqcode.cn/autoSkip/md5")
                    .build();
            Response responseMd5 = CacheTools.getInstance().getClient().newCall(requestMd5).execute();
            String bodyMd5 = responseMd5.body().string();
            Result resultMd5 = JSON.parseObject(bodyMd5, Result.class);
            String md5 = ValueTools.build().getString("appConfigMd5");
            if (md5 != null) if (md5.equals(resultMd5.getData().toString())) return;
            Request request = new Request.Builder()
                    .url("https://api.lqcode.cn/autoSkip/get")
                    .build();
            Response response = CacheTools.getInstance().getClient().newCall(request).execute();
            String result = response.body().string();
            NetApps app = JSON.parseObject(result, NetApps.class);
            XController.getInstance().getDb().appConfigDao().delAll();
            for (String key : app.getData().keySet()) {
                String value = app.getData().get(key);
                DBAppConfig appConfig = new DBAppConfig();
                appConfig.setButtonName(value);
                appConfig.setPackageActivity(key);
                XController.getInstance().getDb().appConfigDao().addAppConfig(appConfig);
            }
            Log.d(TAG, "getApps: " + XController.getInstance().getDb().appConfigDao().getAll());
            CacheTools.getInstance().setApps(app.getData());
            ValueTools.build().putString("appConfigMd5", resultMd5.getData().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String text = "跳过广告 | 5";
        text = text.toString().replace(" ", "");
        String pattern = "^[0-9]跳过";
        String pattern002 = "^跳过";
        boolean isMatches = Pattern.matches(pattern, text);
        boolean isMatches002 = Pattern.matches(pattern002, text);
        System.out.println(isMatches);
        System.out.println(isMatches002);
    }

}
