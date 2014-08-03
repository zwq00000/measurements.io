package com.redriver.measurements.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;

/**
 * Toast 管理器
 * 用于显示 Toast 消息
* Created by zwq00000 on 2014/7/16.
*/
public class ToastManager implements Runnable {
    private static final String TAG = "ToastManager";
    private final Context mContext;
    @NotNull
    private final Handler mHandler;
    private String mMessage;
    private Toast mToast;

    public ToastManager(Context context) {
        this.mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 显示消息
     * @param msg
     */
    public void show(String msg) {
        mMessage = msg;
        mHandler.removeCallbacks(this);
        mHandler.post(this);
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        Log.d(TAG, "当前线程：" + Thread.currentThread().getName() + ", 活动线程数量："
                + Thread.activeCount() + ", Msg:" + mMessage);
         if (mToast == null) {
            mToast = createToast();
        } else {
            updateToast(mToast);
        }
        mToast.show();
    }

    /**
     * 创建 Toast 实例,创建过程中 可以自定义 Toast 样式{@link android.widget.Toast#setView(android.view.View)}
     * 默认构造方式 {@link android.widget.Toast#makeText(android.content.Context, CharSequence, int)}
     * 默认显示位置 {@link android.view.Gravity#CENTER} 屏幕中央
     * @return
     */
    @NotNull
    protected Toast createToast(){
        Toast toast = Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    /**
     * 更新 Toast 内容，参考 {@link android.widget.Toast#setText(CharSequence)}
     * 设置显示时间 {@link android.widget.Toast#setDuration(int)}
     * @param toast 已创建的 Toast 实例
     */
    protected void updateToast(@NotNull Toast toast){
        toast.setText(mMessage);
        mToast.setDuration(Toast.LENGTH_SHORT);
    }

    /**
     * 取消 Toast 显示
     */
    public synchronized void cancelToast() {
        if (mToast != null) {
            mHandler.removeCallbacks(this);
            mToast.cancel();
            Log.d(TAG, "cancel Toast");
        }
    }
}
