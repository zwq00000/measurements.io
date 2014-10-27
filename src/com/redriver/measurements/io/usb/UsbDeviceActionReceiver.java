package com.redriver.measurements.io.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.redriver.measurements.io.FrameReceiverApplication;
import com.redriver.measurements.io.FrameReceiverPreferences;

import java.io.IOException;

/**
 * Usb 设备活动接收器
 * Created by zwq00000 on 2014/10/27.
 */
public class UsbDeviceActionReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbDeviceActionReceiver";
    private static final String ACTION_USB_PERMISSION =
            "com.redriver.action.USB_PERMISSION";
    private String mDeviceName;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        String deviceName = usbDevice.getDeviceName();

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbSerialDriver serialDriver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
            if (serialDriver != null) {
                mDeviceName = deviceName;
                Log.d(TAG,"Attached Usb Device " + mDeviceName);
                FrameReceiverPreferences.getInstance(context).setUseUsbReceiver(true);
                FrameReceiverApplication app = (FrameReceiverApplication) context.getApplicationContext();
                if(app!= null){
                    app.initFrameReceiver();
                }
            }
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (deviceName.equals(mDeviceName)) {
                Log.d(TAG,"DETACHED Usb Device " + mDeviceName);
                FrameReceiverPreferences.getInstance(context).setUseUsbReceiver(false);
            }
        }
    }
}
