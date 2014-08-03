package com.redriver.measurements.io.usb;

import android.app.Application;
import com.redriver.measurements.io.FrameReceiver;

import java.io.IOException;

/**
 * Created by zwq00000 on 2014/7/25.
 */
public class UsbSerialPortApplication extends Application {

    private UsbFrameReceiver mUsbFrameReceiver;

    @Override
    public void onCreate() {

        mUsbFrameReceiver = new UsbFrameReceiver(this);
        try {
            mUsbFrameReceiver.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        try {
            if(mUsbFrameReceiver!=null) {
                mUsbFrameReceiver.Terminate();
                mUsbFrameReceiver.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mUsbFrameReceiver = null;
        }
    }

    public FrameReceiver getFrameReceiver() {
        return mUsbFrameReceiver;
    }
}
