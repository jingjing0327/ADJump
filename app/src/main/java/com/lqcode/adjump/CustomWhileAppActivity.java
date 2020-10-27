package com.lqcode.adjump;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.lqcode.adjump.entity.db.DBCustomWhileAppEntity;
import com.lqcode.adjump.frame.XController;

public class CustomWhileAppActivity extends AppCompatActivity {
    private static final String TAG = CustomWhileAppActivity.class.getSimpleName();
    SwitchCompat switchCompat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_app);
        String packageName = getIntent().getStringExtra("packageName");
        switchCompat = findViewById(R.id.while_switch);
        settingAppStatus(packageName);
        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            settingAppState(b, packageName);
        });
    }


    private void settingAppState(boolean b, String packageName) {
        new Thread(() -> {
            if (b) {
                DBCustomWhileAppEntity whileListAppConfig = new DBCustomWhileAppEntity();
                whileListAppConfig.setPackageName(packageName);
                XController.getInstance().getDb().whileAppConfigDao().add(whileListAppConfig);
            } else {
                XController.getInstance().getDb().whileAppConfigDao().delByPackageName(packageName);
            }
            Log.d(TAG, "settingAppState: " + XController.getInstance().getDb().whileAppConfigDao().getAll());
        }).start();
    }

    /**
     * @param packageName
     */
    private void settingAppStatus(String packageName) {
        new Thread(() -> {
            int count = XController.getInstance().getDb().whileAppConfigDao().getCountByPackage(packageName);
            switchCompat.setChecked(count > 0);
        }).start();

    }
}
