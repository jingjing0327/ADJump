package com.lqcode.adjump.tools;

import java.util.UUID;
import java.util.regex.Pattern;

public class Tools {
    public static String getDeviceId() {
        String deviceId = ValueTools.build().getString("deviceId");
        if (deviceId == null) {
            deviceId = android.os.Build.BRAND + "-" + android.os.Build.MODEL + "-" + UUID.randomUUID().toString();
            ValueTools.build().putString("deviceId", deviceId);
        }
        return deviceId;
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
