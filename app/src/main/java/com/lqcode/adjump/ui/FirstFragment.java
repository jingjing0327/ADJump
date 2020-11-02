package com.lqcode.adjump.ui;

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

import com.lqcode.adjump.R;
import com.lqcode.adjump.tools.ValueTools;

public class FirstFragment extends Fragment {
    private static final String TAG = FirstFragment.class.getSimpleName();
    private Intent intent;
    private SwitchCompat serviceSwitch;
    private TextView jumpCount;


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

        TextView   serviceText = rootView.findViewById(R.id.service_text);
        serviceText.setText(serviceSwitch.isChecked() ? "服务正在运行" : "服务未开启，点击开启服务");
        jumpCount = rootView.findViewById(R.id.jump_count);
        rootView.findViewById(R.id.setting_tv).setOnClickListener(view -> startActivity(new Intent(getContext(), SettingActivity.class)));
        rootView.findViewById(R.id.about_tv).setOnClickListener(view -> startActivity(new Intent(getContext(), AboutActivity.class)));
        rootView.findViewById(R.id.test_tv).setOnClickListener(view -> startActivity(new Intent(getContext(), LoginActivity.class)));
        rootView.findViewById(R.id.custom_tv).setOnClickListener(view -> startActivity(new Intent(getContext(), CustomActivity.class)));

        return rootView;
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