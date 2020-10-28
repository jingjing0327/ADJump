package com.lqcode.adjump.tools;

import android.os.Environment;
import android.util.Log;

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
        }).start();
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
