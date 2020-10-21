package com.lqcode.adjump;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;


import com.alibaba.fastjson.JSON;
import com.lqcode.adjump.entity.Result;
import com.lqcode.adjump.entity.db.DBAutoJumpConfig;
import com.lqcode.adjump.event.LayoutMessage;
import com.lqcode.adjump.event.RemoveLayoutMessage;
import com.lqcode.adjump.frame.CacheTools;
import com.lqcode.adjump.frame.XController;
import com.lqcode.adjump.tools.ValueTools;
import com.lqcode.adjump.ui.Kuang;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @功能:
 * @Creat 2019/12/2 10:16
 * @User Lmy
 * @Compony zaituvideo
 */
public class SkipService extends AccessibilityService {
    private static final String TAG = "SkipService";
    private boolean isDebug = true;

    private static final List<String> PageWidgetViews = new ArrayList<String>() {
        {
            add("android.widget.RelativeLayout");
            add("android.widget.LinearLayout");
            add("android.widget.FrameLayout");
            add("android.widget.TableLayout");
            add("android.widget.AbsoluteLayout");
            add("android.widget.GridLayout");
            add("android.widget.ConstraintLayout");
            add("android.widget.TextView");
            add("android.widget.ConstraintLayout");
            add("android.widget.ImageView");
        }
    };

    private Kuang kuangView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RemoveLayoutMessage message) {
        if (winManager != null && kuangView != null) {
            winManager.removeView(kuangView);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LayoutMessage event) {

        myFindWidth.clear();
        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        Log.d(TAG, "onMessageEvent: " + event.toString());
        findAllViews(rowNode);

        List<Rect> rectList = new ArrayList<>();

        for (AccessibilityNodeInfo nodeInfo : myFindWidth) {
            Log.d(TAG, "findAllViews: " + nodeInfo.getText());
            Rect rect = new Rect();
            nodeInfo.getBoundsInScreen(rect);
            Log.d(TAG, "onMessageEvent: " + rect.top + "===>" + rect.bottom + "===>" + rect.left + "===>" + rect.right);
            if ((rect.bottom - rect.top) >= CacheTools.getInstance().getHeight() * 0.5)
                continue;

            if (rect.right - rect.left >= CacheTools.getInstance().getWidth() * 0.9)
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
//                else if (rect.contains(rectItem)) {
//
//
//                }
            }
            if (isContains)
                continue;

            rectList.add(rect);
        }

        kuangView = new Kuang(this, rectList);
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
                    if (PageWidgetViews.contains(item.getClassName().toString())) {
                        myFindWidth.add(item);
                    }

                if (item.getChildCount() > 0) {
                    findAllViews(item);
                }
            }
        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.d(TAG, "onAccessibilityEvent: ===================================================");
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d(TAG, "onAccessibilityEvent: event.getClassName()==>" + event.getClassName());
            Log.d(TAG, "onAccessibilityEvent: event.getPackageName()==>" + event.getPackageName());
            Log.d(TAG, "=========================================");
            String key = event.getPackageName() + "-" + event.getClassName();
            if (CacheTools.getInstance().getApps() != null && CacheTools.getInstance().getApps().containsKey(key)) {
                Log.d(TAG, "onAccessibilityEvent: 开始查找！");
                AccessibilityNodeInfo nodeInfo = event.getSource();//当前界面的可访问节点信息
                if (nodeInfo == null) return;
                String ids = CacheTools.getInstance().getApps().get(key);
                if (ids == null) return;
                if (ids.length() == 0) return;
                skip(ids, nodeInfo,event.getClassName().toString());
            }

            if (isDebug) {
                AccessibilityNodeInfo nodeInfo = event.getSource();
                if (nodeInfo == null) return;
                findJumpText(nodeInfo, event.getClassName().toString());
            }
        }
    }

    /**
     * @param nodeInfo
     */
    private void findJumpText(final AccessibilityNodeInfo nodeInfo, String className) {
        List<AccessibilityNodeInfo> accessibilityNodeInfoList = nodeInfo.findAccessibilityNodeInfosByText("跳过");
        if (accessibilityNodeInfoList.size() <= 0) {
            new Handler().postDelayed(() -> {
                if (count <= 80) {
                    findJumpText(nodeInfo, className);
                    count++;
                } else {
                    count = 0;
                }
            }, 200);
        } else {
            AccessibilityNodeInfo findNodeInfo = accessibilityNodeInfoList.get(0);
            Log.e(TAG, "find text it!id is ===>>" + findNodeInfo.getViewIdResourceName());
            CharSequence text = findNodeInfo.getText();
            Log.e(TAG, "find text it!id is text ===>>" + text);
            if (text.length() <= 5) {
                skipClick(accessibilityNodeInfoList);
                addAutoJumpDB(findNodeInfo, className);
            }
            count = 0;
        }
    }

    /**
     *
     */
    private void addAutoJumpDB(AccessibilityNodeInfo findNodeInfo, String className) {
        new Thread(() -> {
            String packageActivity = findNodeInfo.getPackageName() + "-" + className;
            List<DBAutoJumpConfig> dbAutoJumpConfigList = XController.getInstance().getDb().autoJumpConfigDao().getByPackageActivity(packageActivity);
            if (dbAutoJumpConfigList.size() <= 0) {
                DBAutoJumpConfig dbAutoJumpConfig = new DBAutoJumpConfig();
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
            List<DBAutoJumpConfig> dbAutoJumpConfigList =
                    XController.getInstance().getDb().autoJumpConfigDao().getAll();
            RequestBody body = RequestBody.create(JSON.toJSONString(dbAutoJumpConfigList), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("http://api.lqcode.cn/autoSkip/upload")
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


    int count = 1;

    private void findNodeInfoViewById(final AccessibilityNodeInfo nodeInfo, final String id) {
        List<AccessibilityNodeInfo> accessibilityNodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (accessibilityNodeInfoList.size() <= 0) {
            new Handler().postDelayed(() -> {
                if (count <= 80) {
                    findNodeInfoViewById(nodeInfo, id);
                    count++;
                } else {
                    count = 0;
                }
            }, 200);
        } else {
            Log.e(TAG, "find id it!");
            skipClick(accessibilityNodeInfoList);
            count = 0;
        }
    }

    private void skip(String ids, AccessibilityNodeInfo nodeInfo, String className) {
        for (String id : ids.split(",")) {
            Log.d(TAG, "skip: 查找id为：" + id);
            if (id.equals("-1"))
                findJumpText(nodeInfo, className);
            else
                findNodeInfoViewById(nodeInfo, id);
        }
    }


    private void skipClick(List<AccessibilityNodeInfo> nodeInfoList) {
        boolean isClick = nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        Rect rect = new Rect();
        nodeInfoList.get(0).getBoundsInScreen(rect);
//        Toast.makeText(getApplicationContext(), "跳过广告", Toast.LENGTH_SHORT).show();
        XController.getInstance().toastShow("跳过广告");
        ValueTools.build().putInt("jumpCount", ValueTools.build().getInt("jumpCount") + 1);
//        dispatchGesture(new GestureDescription())
        if (!isClick) {
            onTouch(rect);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onTouch(Rect rect) {
        Log.d(TAG, "====onTouch====");
        Boolean b = dispatchGesture(createClick(rect.left + 1, rect.top + 1), new GestureResultCallback() {
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
        EventBus.getDefault().register(this);
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
