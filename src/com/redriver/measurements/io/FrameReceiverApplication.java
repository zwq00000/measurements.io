package com.redriver.measurements.io;

import android.app.Application;

/**
 * 测量数据帧接收器 应用程序,提供应用程序级的 接收器 资源管理
 * Created by zwq00000 on 2014/7/25.
 */
public class FrameReceiverApplication extends Application {

    private static final String TAG = "FrameReceiverApplication";
    /**
     * 帧数据接收器实例
     */
    private FrameReceiver mFrameReceiver;
    /**
     * 数据侦听器
     */
    private IFrameReceiver.DataReceivedListener mListener;

    @Override
    public void onCreate() {

    }

    /**
     * This method is for use in emulated process environments.  It will
     * never be called on a production Android device, where processes are
     * removed by simply killing them; no user code (including this callback)
     * is executed when doing so.
     */
    @Override
    public void onTerminate() {
        try {
            if (mFrameReceiver != null) {
                mFrameReceiver.Terminate();
            }
        } finally {
            mFrameReceiver = null;
        }
    }

    /**
     * 获取接收器实例
     *
     * @return
     */
    public IFrameReceiver getFrameReceiver() {
        initFrameReceiver();
        return mFrameReceiver;
    }

    /**
     * 初始化接收器
     */
    public void initFrameReceiver() {
        mFrameReceiver = FrameReceiverPreferences.getInstance(this).getFrameReceiver();
        if (mListener != null) {
            mFrameReceiver.setDataReceivedListener(mListener);
        }
        mFrameReceiver.open();
    }

    /**
     * 设置 侦听器
     *
     * @param listener
     */
    public void setDataReceivedListener(IFrameReceiver.DataReceivedListener listener) {
        this.mListener = listener;
        if (mFrameReceiver != null) {
            mFrameReceiver.setDataReceivedListener(listener);
        }
    }
}
