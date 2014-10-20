package com.redriver.measurements.io.test;

import android.test.AndroidTestCase;
import android.util.Log;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.IFrameReceiver;
import com.redriver.measurements.io.serial.SerialPortReceiver;

public class SerialPortReceiverTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testOpen() throws Exception {
        SerialPortReceiver receiver = new SerialPortReceiver(getContext(),"/dev/ttySAC0");
        receiver.setDataReceivedListener(new IFrameReceiver.DataReceivedListener() {
            @Override
            public void onDataReceived(MeasureRecord event) {
                Log.d("SerialPortReceiver",event.toString());
            }
        });
        receiver.open();

        Thread.sleep(1000*100);
    }

    public void testClose() throws Exception {

    }
}