package com.lqcode.adjump.ui;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.entity.db.DBAutoJumpEntity;
import com.lqcode.adjump.entity.db.DBCustomAppEntity;
import com.lqcode.adjump.event.AgentLayoutMessage;
import com.lqcode.adjump.event.LayoutMessage;
import com.lqcode.adjump.event.RemoveLayoutMessage;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.tools.Tools;
import com.lqcode.adjump.tools.ValueTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 */
public class SkipService extends AccessibilityService {
    private static final String TAG = SkipService.class.getSimpleName();
    //todo
    private boolean isDebug = true;
    private Kuang kuangView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RemoveLayoutMessage message) {
        if (winManager != null && kuangView != null) {
            winManager.removeViewImmediate(kuangView);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AgentLayoutMessage message) {
        if (winManager != null && kuangView != null) {
            winManager.removeViewImmediate(kuangView);
            XController.getInstance().getmHandler().postDelayed(() -> {
//                Kuang kuangView001 = new Kuang(SkipService.this, rectList, SkipService.this.lastPackageName, SkipService.this.lastClassName);
//                winManager.addView(kuangView001, wmParams);
//                SkipService.this.kuangView = kuangView001;
                onMessageEvent(new LayoutMessage());
            }, 100);
            //todo
        }
    }


    private List<Rect> rectList = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LayoutMessage event) {
        myFindWidth.clear();
        rectList.clear();
        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        findAllViews(rowNode);

        for (AccessibilityNodeInfo nodeInfo : myFindWidth) {
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            if ((rect.bottom - rect.top) >= CacheTools.getInstance().getHeight() * 0.5)
                continue;

            if (rect.right - rect.left >= CacheTools.getInstance().getWidth() * 0.8)
                continue;

            if (rect.right - rect.left <= 50)
                continue;

            if (rect.bottom - rect.top <= 50)
                continue;

            boolean isContains = false;
            for (Rect rectItem : rectList) {
                if (rectItem.contains(rect)) {
                    isContains = true;
                    break;
                }
            }
            if (isContains)
                continue;
            rectList.add(rect);
        }

        kuangView = new Kuang(this, rectList, this.lastPackageName, this.lastClassName);
        initWindow();
    }

    WindowManager.LayoutParams wmParams;
    WindowManager winManager;

    /**
     * 初始化窗口
     */
    private void initWindow() {
        winManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置好悬浮窗的参数
        wmParams = getParams();
        // 悬浮窗默认显示以左上角为起始坐标
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0
        wmParams.x = 0;
        wmParams.y = 0;
        // 添加悬浮窗的视图
        winManager.addView(kuangView, wmParams);
    }

    private WindowManager.LayoutParams getParams() {
        wmParams = new WindowManager.LayoutParams();
        //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //设置可以显示在状态栏上
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        return wmParams;
    }

    private List<AccessibilityNodeInfo> myFindWidth = new ArrayList<>();

    private void findAllViews(AccessibilityNodeInfo info) {
        if (info.getChildCount() > 0) {
            for (int i = 0; i < info.getChildCount(); i++) {
                AccessibilityNodeInfo item = info.getChild(i);
                if (item.isVisibleToUser())
                    if (item.getClassName().toString().contains("android.widget.") || item.getClassName().toString().contains("androidx.appcompat.widget")) {
                        myFindWidth.add(item);
                    }

                if (item.getChildCount() > 0) {
                    findAllViews(item);
                }
            }
        }
    }

    private String lastPackageName;
    private String lastClassName;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            try {
//                if (event.getClassName().toString().contains("android.widget.")) return;
                if (event.getClassName().toString().contains("android.view.")) return;
                this.lastPackageName = event.getPackageName().toString();
                this.lastClassName = event.getClassName().toString();

                String key = event.getPackageName() + "-" + event.getClassName();
                customAppSkipPosition(key);

                if (CacheTools.getInstance().getApps() == null)
                    Tools.setCacheAppsConfig();

                if (CacheTools.getInstance().getApps() != null && CacheTools.getInstance().getApps().size() <= 0)
                    Tools.setCacheAppsConfig();

                if (CacheTools.getInstance().getApps() != null && CacheTools.getInstance().getApps().containsKey(key)) {
                    AccessibilityNodeInfo nodeInfo = event.getSource();
                    if (nodeInfo == null) return;
                    String ids = CacheTools.getInstance().getApps().get(key);
                    if (ids == null) return;
                    if (ids.length() == 0) return;
                    skip(ids, nodeInfo, event.getClassName().toString(), event.getPackageName().toString());
                }

                if (isDebug) {
                    AccessibilityNodeInfo nodeInfo = event.getSource();
                    if (nodeInfo == null) return;
                    findJumpText(nodeInfo, event.getClassName().toString(), event.getPackageName().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param key
     */
    private void customAppSkipPosition(String key) {
        new Thread(() -> {
            List<DBCustomAppEntity> customAppEntityList = XController.getInstance().getDb().customAppConfigDao().getById(key);
            if (customAppEntityList != null) {
                if (customAppEntityList.size() > 0) {
                    DBCustomAppEntity customAppEntity = customAppEntityList.get(0);
                    Rect rect = new Rect();
                    rect.left = (int) customAppEntity.getLastChooseX();
                    rect.top = (int) customAppEntity.getLastChooseY();
                    XController.getInstance().getmHandler().postDelayed(() -> onTouch(rect), 500);
                }
            }
        }).start();
    }

    /**
     * @param nodeInfo
     */
    private int textCount = 0;

    private void findJumpText(AccessibilityNodeInfo nodeInfo, String className, String packageName) {
        new Thread(() -> {
//        if (!className.equals(this.lastClassName)) return;
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = nodeInfo.findAccessibilityNodeInfosByText("跳过");
            if (accessibilityNodeInfoList.size() <= 0) {
                XController.getInstance().getmHandler().postDelayed(() -> {
                    if (textCount <= 50) {
                        findJumpText(nodeInfo, className, packageName);
                        textCount++;
                    } else {
                        textCount = 0;
                    }
                }, 100);
            } else {
                AccessibilityNodeInfo findNodeInfo = accessibilityNodeInfoList.get(0);
                CharSequence text = findNodeInfo.getText();
                if (text.length() <= 10) {
                    text = text.toString().replace(" ", "");
                    String pattern = "^[0-9]跳过[\\s\\S]*";
                    String pattern002 = "^跳过[\\s\\S]*";
                    if (Pattern.matches(pattern, text) || Pattern.matches(pattern002, text)) {
                        skipClick(accessibilityNodeInfoList);
                        addAutoJumpDB(findNodeInfo, className);
                    }
                }
                textCount = 0;
            }
        }).start();
    }


    /**
     *
     */
    private void addAutoJumpDB(AccessibilityNodeInfo findNodeInfo, String className) {
        new Thread(() -> {
            String packageActivity = findNodeInfo.getPackageName() + "-" + className;
            List<DBAutoJumpEntity> dbAutoJumpConfigList = XController.getInstance().getDb().autoJumpConfigDao().getByPackageActivity(packageActivity);
            if (dbAutoJumpConfigList.size() <= 0) {
                DBAutoJumpEntity dbAutoJumpConfig = new DBAutoJumpEntity();
                dbAutoJumpConfig.setPackageActivity(packageActivity);
                dbAutoJumpConfig.setButtonName(findNodeInfo.getViewIdResourceName() == null ? "-1" : findNodeInfo.getViewIdResourceName());
                XController.getInstance().getDb().autoJumpConfigDao().addAutoJumpConfig(dbAutoJumpConfig);
                uploadCustomAutoJumpAppConfig();
            }
        }).start();
    }

    /**
     *
     */
    private void uploadCustomAutoJumpAppConfig() {
        int count = XController.getInstance().getDb().autoJumpConfigDao().getCount();
        if (count >= 1) {
            List<DBAutoJumpEntity> dbAutoJumpConfigList =
                    XController.getInstance().getDb().autoJumpConfigDao().getAll();
            RequestBody body = RequestBody.create(JSON.toJSONString(dbAutoJumpConfigList), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://api.lqcode.cn/autoSkip/upload")
                    .post(body)
                    .build();
            try {
                Response response = CacheTools.getInstance().getClient().newCall(request).execute();
                Result result = JSON.parseObject(response.body().string(), Result.class);
                if (result.getStatus() == 200) {
                    XController.getInstance().getDb().autoJumpConfigDao().delAll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    int countId = 1;

    private void findNodeInfoViewById(final AccessibilityNodeInfo nodeInfo, final String id, String className) {
        new Thread(() -> {
//        if (!className.equals(this.lastClassName)) return;
            List<AccessibilityNodeInfo> accessibilityNodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(id);
            if (accessibilityNodeInfoList.size() <= 0) {
                XController.getInstance().getmHandler().postDelayed(() -> {
                    if (countId <= 50) {
                        findNodeInfoViewById(nodeInfo, id, className);
                        countId++;
                    } else {
                        countId = 0;
                    }
                }, 100);
            } else {
                Log.e(TAG, "find id it!");
                skipClick(accessibilityNodeInfoList);
                countId = 0;
            }
        }).start();
    }

    /**
     * @param ids
     * @param nodeInfo
     * @param className
     * @param packageName
     */
    private void skip(String ids, AccessibilityNodeInfo nodeInfo, String className, String packageName) {
        for (String id : ids.split(",")) {
            if (id.equals("-1"))
                findJumpText(nodeInfo, className, packageName);
            else
                findNodeInfoViewById(nodeInfo, id, className);
        }
    }

    /**
     * @param nodeInfoList
     */
    private void skipClick(List<AccessibilityNodeInfo> nodeInfoList) {
        new Thread(() -> {
            this.lastClassName = null;
            boolean isClick = nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Rect rect = new Rect();
            nodeInfoList.get(0).getBoundsInScreen(rect);
//        Toast.makeText(getApplicationContext(), "跳过广告", Toast.LENGTH_SHORT).show();
            if (ValueTools.build().getInt("jump_toast_switch") <= 0)
                XController.getInstance().toastShow("跳过广告");
            ValueTools.build().putInt("jumpCount", ValueTools.build().getInt("jumpCount") + 1);
//        dispatchGesture(new GestureDescription())
            if (!isClick) {
                onTouch(rect);
            }
        }).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onTouch(Rect rect) {
        Log.d(TAG, "====onTouch====");
        if (rect.bottom > 0 && rect.right > 0) {
            int rectHeight = rect.bottom - rect.top;
            int rectWidth = rect.right - rect.left;
            rect.left = rect.left + (rectWidth / 3);
            rect.top = rect.top + (rectHeight / 3);
        }

        Boolean b = dispatchGesture(createClick(rect.left, rect.top), new GestureResultCallback() {
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.d(TAG, "onCancelled====");
            }

            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.d(TAG, "onCompleted===");
            }
        }, null);

        Log.d(TAG, "dispatchGesture====>>>" + b.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private GestureDescription createClick(float x, float y) {
        // for a single tap a duration of 1 ms is enough
        final int DURATION = 1;

        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.StrokeDescription clickStroke =
                new GestureDescription.StrokeDescription(clickPath, 0, DURATION);
        GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
        clickBuilder.addStroke(clickStroke);
        return clickBuilder.build();
    }

    @Override
    protected void onServiceConnected() {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
//        //配置监听的事件类型为界面变化|点击事件
//        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
//        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
//        setServiceInfo(config);
        super.onServiceConnected();
    }

    @Override
    public void onInterrupt() {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
