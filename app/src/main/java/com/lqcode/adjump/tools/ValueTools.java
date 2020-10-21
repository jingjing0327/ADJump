package com.lqcode.adjump.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.lqcode.adjump.frame.CacheTools;


/**
 * Created by LiQiong on 2018/5/24 20:23
 */

public class ValueTools {
    private static SharedPreferences sp = null;
    private static ValueTools value = null;
    private static final String CONFIG_NAME = "conf";

    /**
     *
     */
    private ValueTools() {
        sp = CacheTools.getInstance().getContext().getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @return
     */
    public static ValueTools build() {
        if (value == null)
            value = new ValueTools();
        return value;
    }

    /**
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * @param key
     * @return
     */
    public String getString(String key) {
        try {
            return sp.getString(key, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param key
     * @param value
     */
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(String key, int defaultValue) {
        int ret = defaultValue;
        try {
            ret = sp.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * @param key
     * @return
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

}
