package com.lqcode.adjump.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.lqcode.adjump.R;
import com.lqcode.adjump.tools.ValueTools;
import com.tencent.bugly.beta.Beta;

import java.util.List;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("关于");
        TextView versionTV = findViewById(R.id.version);

        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        versionTV.setText("检查版本号   " + version);
        findViewById(R.id.version_layout).setOnClickListener(view -> Beta.checkUpgrade());
        findViewById(R.id.contact_layout).setOnClickListener(view -> customerService());
        View payLayout = findViewById(R.id.pay_layout);
        payLayout.setOnClickListener(view -> startActivity(new Intent(AboutActivity.this, VIPActivity.class)));
        payLayout.setVisibility(ValueTools.build().getInt("vip") == 1 ? View.GONE : View.VISIBLE);
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    private boolean isQQClientAvailable() {
        final PackageManager packageManager = getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断 Uri是否有效
     */
    private boolean isValidIntent(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }

    private void customerService() {
        // 跳转之前，可以先判断手机是否安装QQ
        if (isQQClientAvailable()) {
            // 跳转到客服的QQ
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=2061878685";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // 跳转前先判断Uri是否存在，如果打开一个不存在的Uri，App可能会崩溃
            if (isValidIntent(intent)) {
                startActivity(intent);
            }
        }
    }
}
