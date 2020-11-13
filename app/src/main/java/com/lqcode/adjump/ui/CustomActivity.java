package com.lqcode.adjump.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lqcode.adjump.FloatWinfowServices;
import com.lqcode.adjump.R;
import com.lqcode.adjump.entity.db.DBCustomAppEntity;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class CustomActivity extends BaseActivity {

    private boolean hasBind = false;
    private boolean isStart = false;
    private List<DBCustomAppEntity> configList = new ArrayList<>();
    MyAdapter myAdpapter;
    private final int SCREEN_CAPTURE = 123;
    List<PackageInfo> packageInfoList;
    private Button create;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("自定义规则");

        create = findViewById(R.id.create);
        create.setOnClickListener(this::zoom);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(myAdpapter = new MyAdapter(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        xx();

        PackageManager pm = getPackageManager();
        packageInfoList = pm.getInstalledPackages(0);
    }


    private void xx() {
        new Thread(() -> {
            List<DBCustomAppEntity> dbCustomAppEntityList = XController.getInstance().getDb().customAppConfigDao().getAll();
            if (dbCustomAppEntityList.size() != configList.size()) {
                configList.clear();
                configList.addAll(dbCustomAppEntityList);
                myAdpapter.notifyDataSetChanged();
            }
        }).start();
    }

    private Intent intent;

    private void zoom(View v) {
        if (isStart) {
            mediaProjection.stop();
            unbindService(mVideoServiceConnection);
            isStart = false;
            create.setText("开始创建");
        } else {
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
                isStart = true;
                intent = new Intent(this, FloatWinfowServices.class);
                hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                startScreenShot();
            }
        }
    }

    private MediaProjectionManager mediaProjectionManager;
    private int dpi;

    private void startScreenShot() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        CacheTools.getInstance().setWidth(width);
        int height = displayMetrics.heightPixels;
        CacheTools.getInstance().setHeight(height);
        dpi = displayMetrics.densityDpi;

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager != null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE);
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

    Image image;
    Bitmap bitmap;
    MediaProjection mediaProjection;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                XController.getInstance().getmHandler().postDelayed(() -> {
                    isStart = true;
                    Intent intent = new Intent(CustomActivity.this, FloatWinfowServices.class);
                    hasBind = bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
                    startScreenShot();
                }, 500);
            }
        }
        if (requestCode == SCREEN_CAPTURE) {
            try {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                ImageReader imageReader = ImageReader.newInstance(CacheTools.getInstance().getWidth(), CacheTools.getInstance().getHeight(), PixelFormat.RGBA_8888, 2);
                mediaProjection.createVirtualDisplay("screen_shot",
                        CacheTools.getInstance().getWidth(), CacheTools.getInstance().getHeight(), dpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY,
                        imageReader.getSurface(), null, null);

                imageReader.setOnImageAvailableListener(reader -> {
                    image = reader.acquireNextImage();
                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * CacheTools.getInstance().getWidth();
                    bitmap = Bitmap.createBitmap(CacheTools.getInstance().getWidth() + rowPadding / pixelStride, CacheTools.getInstance().getHeight(), Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    image.close();
                    CacheTools.getInstance().setBitmap(bitmap);
                }, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    protected void onResume() {
        super.onResume();
        xx();
        create.setText(isStart ? "停止创建" : "开始创建");
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

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_app_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            DBCustomAppEntity customAppEntity = configList.get(position);
            String[] packageNameActivity = customAppEntity.getPackageActivity().split("-");
            String packageName = packageNameActivity[0];
            String activityName = packageNameActivity[1];

            for (PackageInfo packageInfo : packageInfoList) {
                if (packageInfo.packageName.equals(packageName)) {
                    holder.appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                    holder.appName.setText(packageInfo.applicationInfo.loadLabel(getPackageManager()));
                    holder.appInfo.setText(activityName + "\nx坐标：" + customAppEntity.getLastChooseX() + "   Y坐标：" + customAppEntity.getLastChooseY());
                }
            }

            holder.itemView.setOnLongClickListener(view -> {
                new AlertDialog.Builder(CustomActivity.this)
                        .setTitle("提示").setMessage("是否删除").setPositiveButton("删除", (dialogInterface, i) -> new Thread(() -> {
                    XController.
                            getInstance().
                            getDb().
                            customAppConfigDao().
                            delete(customAppEntity);
                    XController.
                            getInstance().
                            getmHandler().
                            post(() -> {
                                notifyItemRemoved(position);
                                configList.remove(position);
                            });
                }).start())
                        .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss()).show();

                return false;
            });
//            holder.itemView.setOnClickListener(view -> new AlertDialog.Builder(CustomActivity.this)
//                    .setTitle("提示").setMessage("是否删除").setPositiveButton("删除", (dialogInterface, i) -> new Thread(() -> {
//                        XController.
//                                getInstance().
//                                getDb().
//                                customAppConfigDao().
//                                delete(customAppEntity);
//                        XController.
//                                getInstance().
//                                getmHandler().
//                                post(() -> {
//                                    notifyItemRemoved(position);
//                                    configList.remove(position);
//                                });
//                    }).start())
//                    .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss()).show());
        }

        @Override
        public int getItemCount() {
            return configList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView appName;
            TextView appInfo;
            ImageView appIcon;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                appName = itemView.findViewById(R.id.app_name);
                appInfo = itemView.findViewById(R.id.app_info);
                appIcon = itemView.findViewById(R.id.app_icon);
            }
        }
    }
}
