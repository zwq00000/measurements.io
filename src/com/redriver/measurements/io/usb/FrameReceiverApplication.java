package com.redriver.measurements.io.usb;

import android.app.Application;
import com.redriver.measurements.io.FrameReceiver;
import com.redriver.measurements.io.IFrameReceiver;

import java.io.IOException;

/**
 * Usb 串口 应用程序,提供应用程序级的 Usb 端口资源管理
 * Created by zwq00000 on 2014/7/25.
 */
public class FrameReceiverApplication extends Application {

    private FrameReceiver mFrameReceiver;

    @Override
    public void onCreate() {

        mFrameReceiver = FrameReceiver.CreateReceiver(this);
        try {
            mFrameReceiver.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if(mFrameReceiver !=null) {
                mFrameReceiver.Terminate();
            }
        }finally {
            mFrameReceiver = null;
        }
    }

    public IFrameReceiver getFrameReceiver() {
        return mFrameReceiver;
    }
}
