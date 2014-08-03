package com.redriver.measurements.warnningLamp;

import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;

/**
 * 警示灯接收器代理，负责构造和发送 警示灯操作广播
 *
 * Created by zwq00000 on 2014/6/27.
 */
public class WarningLampReceiverProxy {

    /**
     * 警示灯 Action
     */
    public static final String ACTION_WARNING_LAMP = "com.redriver.WARNING_LAMP";

    /**
     * 警报级别 Extra Key
     */
    public static final String KEY_WARNING_LEVEL = "warningLevel";

    /**
     * 普通活动响应,默认行为 蜂鸣器 短鸣一声
     */
    private static final int LAMP_MODE_ACTION = 0;

    /**
     * 警告, 默认行为 蜂鸣器长鸣一声，亮黄灯
     */
    private static final int LAMP_MODE_WARNING = 1;
    /**
     * 异常，默认行为 蜂鸣器闪鸣，红灯闪亮
     */
    private static final int LAMP_MODE_ERROR = 2;

    /**
     * 提醒，提示特殊操作，默认行为 蜂鸣器端鸣两声
     */
    private static final int LAMP_MODE_NOTIFY = 4;

    /**
     * 关闭警报指示,保持静默状态
     */
    private static final int LAMP_MODE_SILENCE = -1;

    /**
     * 错误警示消息
     */
    public static final Intent INTENT_ERROR = new Intent(ACTION_WARNING_LAMP);

    /**
     * 正常活动警示消息
     */
    public static final Intent INTENT_ACTION = new Intent(ACTION_WARNING_LAMP);

    /**
     * 警告警示消息
     */
    public static final Intent INTENT_WARNING = new Intent(ACTION_WARNING_LAMP);

    /**
     * 提示消息
     */
    public static final Intent INTENT_NOTIFY = new Intent(ACTION_WARNING_LAMP);

    /**
     * 关闭警报器
     */
    public static final Intent INTENT_SILENCE = new Intent(ACTION_WARNING_LAMP);

    static {
        INTENT_ERROR.putExtra(KEY_WARNING_LEVEL, LAMP_MODE_ERROR);
        INTENT_ACTION.putExtra(KEY_WARNING_LEVEL, LAMP_MODE_ACTION);
        INTENT_WARNING.putExtra(KEY_WARNING_LEVEL, LAMP_MODE_WARNING);
        INTENT_NOTIFY.putExtra(KEY_WARNING_LEVEL, LAMP_MODE_NOTIFY);
        INTENT_SILENCE.putExtra(KEY_WARNING_LEVEL, LAMP_MODE_SILENCE);
    }

    /**
     * 静默
     * @param context
     */
    public static void activeSilence(@NotNull Context context){
        context.sendBroadcast(INTENT_SILENCE);
    }

    /**
     * 活动
     * @param context
     */
    public static void activeAction(@NotNull Context context){
        context.sendBroadcast(INTENT_ACTION);
    }

    /**
     * 警告
     * @param context
     */
    public static void activeWarning(@NotNull Context context){
        context.sendBroadcast(INTENT_WARNING);
    }

    /**
     * 错误
     * @param context
     */
    public static void activeError(@NotNull Context context){
        context.sendBroadcast(INTENT_ERROR);
    }

    /**
     * 提示
     * @param context
     */
    public static void activeNotify(@NotNull Context context){
        context.sendBroadcast(INTENT_NOTIFY);
    }
}
