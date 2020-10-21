package com.lqcode.adjump;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lqcode.adjump.entity.NetApps;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.entity.db.DBAppConfig;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.tools.ValueTools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CacheTools.getInstance().setContext(this);
        XController.getInstance();

        setCacheAppsConfig();
        getNewApps();
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}