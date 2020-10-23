package com.lqcode.adjump;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lqcode.adjump.event.LayoutMessage;
import com.lqcode.adjump.event.ScreenMessage;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;


public class CustomActivity extends AppCompatActivity {

    private boolean hasBind = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        EventBus.getDefault().register(this);
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
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 111);
            }).setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss()).show();

        } else {
            Intent intent = new Intent(this, FloatWinfowServices.class);
            hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
            startScreenShot();
        }
    }

    MediaProjectionManager mediaProjectionManager;
    int width;
    int height;
    int dpi;

    private void startScreenShot() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        CacheTools.getInstance().setWidth(width);
        height = displayMetrics.heightPixels;
        CacheTools.getInstance().setHeight(height);
        dpi = displayMetrics.densityDpi;

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager != null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 123);
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
        if (requestCode == 111) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                XController.getInstance().getmHandler().postDelayed(() -> {
                    Intent intent = new Intent(CustomActivity.this, FloatWinfowServices.class);
                    hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                    startScreenShot();
                }, 1000);
            }
        }
        if (requestCode == 123) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            imageReader = ImageReader.newInstance(CacheTools.getInstance().getWidth(), CacheTools.getInstance().getHeight(), PixelFormat.RGBA_8888, 2);
            mediaProjection.createVirtualDisplay("screen_shot",
                    CacheTools.getInstance().getWidth(), CacheTools.getInstance().getHeight(), dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY,
                    imageReader.getSurface(), null, null);

            imageReader.setOnImageAvailableListener(reader -> {
                Image image = reader.acquireNextImage();
                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * CacheTools.getInstance().getWidth();
                Bitmap bitmap = Bitmap.createBitmap(CacheTools.getInstance().getWidth() + rowPadding / pixelStride, CacheTools.getInstance().getHeight(), Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                image.close();
                CacheTools.getInstance().setBitmap(bitmap);
            }, null);
        }
    }


    MediaProjection mediaProjection;
    private ImageReader imageReader;
//    boolean isGetScreen = true;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScreenMessage event) {
//        isGetScreen = true;
//        if (imageReader == null) {
//
//        }
//        if (isGetScreen) {
//
//        }

        EventBus.getDefault().post(new LayoutMessage());
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("RemoteView", "重新显示了");
//不显示悬浮框
//        if (hasBind) {
//            unbindService(mVideoServiceConnection);
//            hasBind = false;
//        }
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
        EventBus.getDefault().unregister(this);
    }
}
