package com.lqcode.adjump.tools;

import java.util.UUID;

public class Tools {
    public static String getDeviceId() {
        String deviceId = ValueTools.build().getString("deviceId");
        if (deviceId == null) {
            deviceId = android.os.Build.BRAND + "-" + android.os.Build.MODEL + "-" + UUID.randomUUID().toString();
            ValueTools.build().putString("deviceId", deviceId);
        }
        return deviceId;
    }
}
