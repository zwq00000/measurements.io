package com.redriver.measurements.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.*;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.widget.ScrollView;
import android.widget.TextView;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.FrameReceiver;
import com.redriver.measurements.io.R;
import com.redriver.measurements.io.usb.BarCodeReader;
import com.redriver.measurements.io.usb.UsbSerialPortApplication;
import com.redriver.measurements.warnningLamp.WarningLampReceiverProxy;

import java.io.IOException;

/**
 * Created by zwq00000 on 2014/7/23.
 */
public class UsbActivity extends Activity {
    private static final String TAG = "UsbActivity";

    static final String MEASURE_RECORD_ACTION = "com.redriver.measurements.MeasureRecord";
    private FrameReceiver mFrameReceive;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
    private BarCodeReader barCodeReader;

    private BroadcastReceiver measureRecordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WarningLampReceiverProxy.activeAction(UsbActivity.this);
            MeasureRecord data = (MeasureRecord)intent.getExtras().get("MeasureRecord");
            final String message = "MeasureRecord[pushType:" + data.getPushType() + ",GageId:" + data.getGageId() + ",RawValue:" + data.getRawValue() + "]";
            Log.d(TAG, message);
            updateLog(message);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);
        mFrameReceive = ((UsbSerialPortApplication) getApplication()).getFrameReceiver();
        mFrameReceive.setDataReceivedListener(new FrameReceiver.DataReceivedListener() {
            @Override
            public void onDataReceived(MeasureRecord data) {
                Intent receivedIntent = new Intent(MEASURE_RECORD_ACTION);
                receivedIntent.putExtra("MeasureRecord",(Parcelable)data);
                UsbActivity.this.sendBroadcast(receivedIntent);
                Log.d(TAG,"send MeasureRecord " + data.getRawValue());
                data.recycle();
            }
        });

        processInitIntent(getIntent());

        barCodeReader = new BarCodeReader(new BarCodeReader.BarCodeReadListener() {
            @Override
            public void onReadCompleted(String barcode) {
                updateLog(barcode);
            }
        });

    }

    private void processInitIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        updateReceivedData("getIntent:" + intent.getAction());
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
            UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (usbDevice != null) {
                String deviceName = usbDevice.getDeviceName();
                updateReceivedData("UsbDevice:" + usbDevice);
                try {
                    mFrameReceive.open();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateLog(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateReceivedData(message);
                mDumpTextView.invalidate();
            }
        });
    }

    private void updateReceivedData(String message) {
        mDumpTextView.append(message);
        mDumpTextView.append("\r\n");
        mScrollView.scrollTo(0,mDumpTextView.getBottom());
    }

    /**
     * 显示状态栏
     */
    private void showStatusBar() {
        sendBroadcast(new Intent("android.intent.action.DISPLAY_STATUS_BAR"));
    }

    @Override
    public void onStart() {
        super.onStart();
        showStatusBar();
        this.registerReceiver(measureRecordReceiver, new IntentFilter(MEASURE_RECORD_ACTION));
    }
    private KeyCharacterMap mFullKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.FULL);
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        barCodeReader.onDispatchKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        processInitIntent(getIntent());
    }

    @Override
    public void onStop() {
        this.unregisterReceiver(measureRecordReceiver);
        super.onStop();
    }
}