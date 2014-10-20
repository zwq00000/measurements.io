package com.redriver.measurements.io;

import android.app.Application;
import android.util.Log;

import java.io.IOException;

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
     * @return
     */
    public IFrameReceiver getFrameReceiver() {
        initFrameReceiver();
        return mFrameReceiver;
    }

    /**
     * 初始化接收器
     */
    private void initFrameReceiver(){
        if(mFrameReceiver == null){
            mFrameReceiver = FrameReceiverPreferences.getInstance(this).getFrameReceiver();
            try {
                mFrameReceiver.open();
            } catch (IOException e) {
                Log.d(TAG,e.getMessage());
                mFrameReceiver = null;
            }
        }
    }
}
