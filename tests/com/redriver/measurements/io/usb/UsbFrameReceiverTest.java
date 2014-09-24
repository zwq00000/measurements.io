package com.redriver.measurements.io.usb;

import android.test.AndroidTestCase;
import android.util.Log;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.IFrameReceiver;
import junit.framework.TestCase;

public class UsbFrameReceiverTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testOpen() throws Exception {
        UsbFrameReceiver receiver = new UsbFrameReceiver(getContext(), new IFrameReceiver.DataReceivedListener() {
            @Override
            public void onDataReceived(MeasureRecord event) {
                Log.d("UsbSerial",event.toString());
            }
        });

        receiver.open();
        Thread.sleep(1000*100);
    }

    public void testClose() throws Exception {

    }

    public void testTerminate() throws Exception {

    }
}