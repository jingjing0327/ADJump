package com.lqcode.adjump.frame;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Map;

import okhttp3.OkHttpClient;

public class CacheTools {
    private static CacheTools cacheTools;
    private String TAG = this.getClass().getSimpleName();

    public static CacheTools getInstance() {
        if (cacheTools == null)
            cacheTools = new CacheTools();
        return cacheTools;
    }

    private Bitmap bitmap;
    private int width;
    private int height;
    private boolean isStartScreen;
    private Map<String, String> apps;
    private Context context;
    private OkHttpClient client;

    public OkHttpClient getClient() {
        if (client == null)
            client = new OkHttpClient();
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Map<String, String> getApps() {
        return apps;
    }

    public void setApps(Map<String, String> apps) {
        this.apps = apps;
    }

    public boolean isStartScreen() {
        return isStartScreen;
    }

    public void setStartScreen(boolean startScreen) {
        isStartScreen = startScreen;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
