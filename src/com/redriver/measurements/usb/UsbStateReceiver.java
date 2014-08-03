package com.redriver.measurements.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.redriver.measurements.ui.ToastManager;

/**
 * Created by zwq00000 on 2014/7/24.
 */
public class UsbStateReceiver extends BroadcastReceiver {

    private static final String TAG = "UsbStateReceiver";
    private static  ToastManager mToastManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mToastManager==null) {
            mToastManager = new ToastManager(context);
        }
        Log.d(TAG,intent.getAction());
        mToastManager.show(intent.getAction());
    }
}
