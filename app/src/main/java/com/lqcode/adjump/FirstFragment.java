package com.lqcode.adjump;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.lqcode.adjump.entity.NetApps;
import com.lqcode.adjump.entity.db.DBAppConfig;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.tools.ValueTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirstFragment extends Fragment {
    private static final String TAG = FirstFragment.class.getSimpleName();
    private Intent intent;
    SwitchCompat serviceSwitch;
    TextView serviceText;
    TextView jumpCount;
    OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);

        intent = new Intent(getContext(), SkipService.class);
        requireContext().startService(intent);

        serviceSwitch = rootView.findViewById(R.id.service_switch);
        serviceSwitch.setChecked(isAccessibilitySettingsOn(requireContext(), SkipService.class.getCanonicalName()));
        serviceSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (!isAccessibilitySettingsOn(requireContext(), SkipService.class.getCanonicalName())) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                } else {
                    //todo
                }
            } else {
                requireContext().stopService(intent);
            }
        });

        serviceText = rootView.findViewById(R.id.service_text);
        serviceText.setText(serviceSwitch.isChecked() ? "服务正在运行" : "服务未开启，点击开启服务");
        jumpCount = rootView.findViewById(R.id.jump_count);
        rootView.findViewById(R.id.setting_tv).setOnClickListener(view -> startActivity(new Intent(getContext(), SettingActivity.class)));
        setCacheAppsConfig();
        getNewApps();
        return rootView;
    }

    /**
     *
     */
    private void setCacheAppsConfig() {
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

    /**
     *
     */
    private void getNewApps() {
        new Thread(() -> {
            try {
                Request requestMd5 = new Request.Builder()
                        .url("http://api.lqcode.cn/autoSkip/md5")
                        .build();
                Response responseMd5 = client.newCall(requestMd5).execute();
                String bodyMd5 = responseMd5.body().string();
                Result resultMd5 = JSON.parseObject(bodyMd5, Result.class);
                String md5 = ValueTools.build().getString("appConfigMd5");
                if (md5 != null) if (md5.equals(resultMd5.getData().toString())) return;
                Request request = new Request.Builder()
                        .url("http://api.lqcode.cn/autoSkip/test")
                        .build();
                Response response = client.newCall(request).execute();
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
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceSwitch.setChecked(isAccessibilitySettingsOn(requireContext(), SkipService.class.getCanonicalName()));
        jumpCount.setText(Html.fromHtml("已成功帮您跳过$次"
                        .replace(
                                "$",
                                "<font color='red'>" + ValueTools.build().getInt("jumpCount") + "</font>"),
                Html.FROM_HTML_MODE_COMPACT));
    }

    /**
     * @param mContext
     * @param serviceName
     * @return
     */
    private boolean isAccessibilitySettingsOn(Context mContext, String serviceName) {
        int accessibilityEnabled = 0;
        // 对应的服务
        String service = mContext.getPackageName() + "/" + serviceName;
        //Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}