package com.lqcode.adjump.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lqcode.adjump.R;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.tools.ValueTools;

import java.util.ArrayList;
import java.util.List;


public class SettingActivity extends BaseActivity {

    @Override
    protected void onStart() {
        super.onStart();
        CacheTools.getInstance().setContext(this);

        jumpToastSwitch.setChecked(ValueTools.build().getInt("jump_toast_switch") > 0);
        jumpToastSwitch.setText(ValueTools.build().getInt("jump_toast_switch") > 0 ? "开" : "关");

        weixinAutoLoginSwitch.setChecked(ValueTools.build().getInt("weixin_auto_login_switch") > 0);
        weixinAutoLoginSwitch.setText(ValueTools.build().getInt("weixin_auto_login_switch") > 0 ? "开" : "关");

    }

    private MyAdapter myAdapter;
    List<PackageInfo> installedPackages = new ArrayList<>();
    SwitchCompat jumpToastSwitch;
    SwitchCompat weixinAutoLoginSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        CacheTools.getInstance().setContext(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        jumpToastSwitch = findViewById(R.id.jump_toast_switch);
        weixinAutoLoginSwitch = findViewById(R.id.weixin_auto_login_switch);


        jumpToastSwitch.setOnClickListener(view -> {
            if (ValueTools.build().getInt("vip") != 1) {
                startActivity(new Intent(SettingActivity.this, VIPActivity.class));
            }
        });

        weixinAutoLoginSwitch.setOnClickListener(view -> {
            if (ValueTools.build().getInt("vip") != 1) {
                startActivity(new Intent(SettingActivity.this, VIPActivity.class));
            }
        });

        jumpToastSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (ValueTools.build().getInt("vip") == 1) {
                ValueTools.build().putInt("jump_toast_switch", b ? 1 : 0);
                jumpToastSwitch.setText(ValueTools.build().getInt("jump_toast_switch") > 0 ? "开" : "关");
            }
        });
        weixinAutoLoginSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (ValueTools.build().getInt("vip") == 1) {
                ValueTools.build().putInt("weixin_auto_login_switch", b ? 1 : 0);
                weixinAutoLoginSwitch.setText(ValueTools.build().getInt("weixin_auto_login_switch") > 0 ? "开" : "关");
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(myAdapter = new MyAdapter(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        PackageManager pm = getPackageManager();
        List<PackageInfo> temp = pm.getInstalledPackages(0);
        List<PackageInfo> tempRemove = new ArrayList<>();
        for (PackageInfo packageInfo : temp) {
            if (packageInfo.packageName.contains("com.android.")) {
                tempRemove.add(packageInfo);
            }
        }
        temp.removeAll(tempRemove);
        installedPackages.addAll(temp);
        myAdapter.notifyDataSetChanged();

    }


    /**
     *
     */
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_app_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PackageInfo packageInfo = installedPackages.get(position);
            holder.appName.setText(packageInfo.applicationInfo.loadLabel(getPackageManager()));
            holder.appIcon.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
            holder.appInfo.setText(packageInfo.packageName + " " + packageInfo.versionName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CustomWhileAppActivity.class);
                    intent.putExtra("packageName", holder.appName.getText());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return installedPackages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView appIcon;
            TextView appName;
            TextView appInfo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                appIcon = itemView.findViewById(R.id.app_icon);
                appName = itemView.findViewById(R.id.app_name);
                appInfo = itemView.findViewById(R.id.app_info);
            }
        }
    }

}
