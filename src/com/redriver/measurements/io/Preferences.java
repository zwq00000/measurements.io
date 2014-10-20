package com.redriver.measurements.io;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 应用程序 首选项
 */
public abstract class Preferences{
    private String preferencesName;
    protected final SharedPreferences preferences;

    protected Preferences(Context context){
        preferencesName = context.getPackageName();
        preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
    }

    /**
     * 设置 字符串 默认值
     * @param key
     * @param value
     */
    protected void setValue(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    protected void setValue(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
