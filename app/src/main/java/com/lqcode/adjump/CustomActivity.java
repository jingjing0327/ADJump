package com.lqcode.adjump;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class CustomActivity extends AppCompatActivity {

    private boolean hasBind = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        findViewById(R.id.create).setOnClickListener(this::zoom);
    }


    public void zoom(View v) {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);

            AlertDialog.Builder builder = new AlertDialog.Builder(CustomActivity.this);
            builder.setTitle("提示");
            builder.setMessage("当前无权限，请授权");
            builder.setCancelable(false);
            builder.setPositiveButton("去开启", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }).setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss()).show();

        } else {
            Intent intent = new Intent(this, FloatWinfowServices.class);
            hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    ServiceConnection mVideoServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取服务的操作对象
            FloatWinfowServices.MyBinder binder = (FloatWinfowServices.MyBinder) service;
            binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CustomActivity.this, FloatWinfowServices.class);
                        hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                        moveTaskToBack(true);
                    }
                }, 1000);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("RemoteView", "重新显示了");
        //不显示悬浮框
        if (hasBind) {
            unbindService(mVideoServiceConnection);
            hasBind = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("RemoteView", "重新显示了onNewIntent");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("RemoteView", "被销毁");
    }
}
