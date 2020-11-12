package com.lqcode.adjump.tools;

import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.entity.db.DBAppConfig;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.frame.net.ApiController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import retrofit2.Call;

public class Tools {
    private static final String TAG = Tools.class.getSimpleName();


    public static void editFocus(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    public static String getDeviceId() {
        String deviceId = ValueTools.build().getString("deviceId");
        if (deviceId == null) {
            deviceId = android.os.Build.BRAND + "-" + android.os.Build.MODEL + "-" + UUID.randomUUID().toString();
            File deviceFile = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.android.settings/lqcodeDevice.file");
            Log.d(TAG, "getDeviceId: " + deviceFile.getAbsolutePath());
            try {
                deviceFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            ValueTools.build().putString("deviceId", deviceId);
        }
        return deviceId;
    }

    public static void setCacheAppsConfig() {
        new Thread(() -> {
            List<DBAppConfig> dbAppConfigList = XController.getInstance().getDb().appConfigDao().getAll();
            if (dbAppConfigList.size() <= 0) {
                getNewApps();
                return;
            }
            if (dbAppConfigList.size() == CacheTools.getInstance().getApps().size())
                return;
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
            Call<Result<String>> md5Call = ApiController.getService().getMd5();
            retrofit2.Response<Result<String>> resultResponse = md5Call.execute();
            Result<String> md5Result = resultResponse.body();
            if (md5Result == null) return;
            String md5 = ValueTools.build().getString("appConfigMd5");
            if (CacheTools.getInstance().getApps().size() <= 0) md5 = UUID.randomUUID().toString();
            if (md5 != null) if (md5.equals(md5Result.getData())) return;

            Call<Result<Map<String, String>>> appConfigCall = ApiController.getService().getAppConfig();
            retrofit2.Response<Result<Map<String, String>>> resultAppConfig = appConfigCall.execute();
            Result<Map<String, String>> appConfigResult = resultAppConfig.body();

            if (appConfigResult == null) return;
            XController.getInstance().getDb().appConfigDao().delAll();
            for (String key : appConfigResult.getData().keySet()) {
                String value = appConfigResult.getData().get(key);
                DBAppConfig appConfig = new DBAppConfig();
                appConfig.setButtonName(value);
                appConfig.setPackageActivity(key);
                XController.getInstance().getDb().appConfigDao().addAppConfig(appConfig);
            }
            Log.d(TAG, "getApps: " + XController.getInstance().getDb().appConfigDao().getAll());
            CacheTools.getInstance().setApps(appConfigResult.getData());
            ValueTools.build().putString("appConfigMd5", md5Result.getData().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String text = "1跳过";
        text = text.toString().replace(" ", "");
        String pattern = "^([0-9]|秒|s|S)跳过[\\s\\S]*";
        String pattern002 = "^跳过";
        boolean isMatches = Pattern.matches(pattern, text);
        boolean isMatches002 = Pattern.matches(pattern002, text);
        System.out.println(isMatches);
        System.out.println(isMatches002);
    }

}
