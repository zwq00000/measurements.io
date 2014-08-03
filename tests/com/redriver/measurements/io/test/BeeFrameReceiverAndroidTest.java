package com.redriver.measurements.io.test;

import android.test.AndroidTestCase;
import android.util.Log;
import android_serialport_api.TtySerialPort;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.BeeFrameReceiver;

import java.io.File;

public class BeeFrameReceiverAndroidTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testAddDataReceivedListener() throws Exception {

    }

    public void testRemoveDataReceivedListener() throws Exception {

    }

    public void testRun() throws Exception {
        TtySerialPort s0 = new TtySerialPort(new File("/dev/ttyS0"), 115200, 0);
        BeeFrameReceiver receiver = new BeeFrameReceiver(s0.getInputStream());
        receiver.setDataReceivedListener(new BeeFrameReceiver.DataReceivedListener() {
            @Override
            public void onDataReceived(MeasureRecord event) {
                //Toast.makeText(getContext(),event.getGageId() + "\t" + event.getRawValue(),Toast.LENGTH_LONG);
                Log.d("BeeFrameReceiver", event.getGageId() + "\t" + event.getRawValue());
                //getContext().sendBroadcast(createReceivedBroadcastIntent(event));
            }
        });
        new Thread(receiver).start();

        Thread.sleep(20* 1000);
    }

    /**
     * 创建 数据接收广播消息
     * @param measureData
     * @return
     */
   /* public static Intent createReceivedBroadcastIntent(MeasureData measureData){
        Intent intent = new Intent(MeasuringTaskService.RECEIVED_MEASURE_DATA);
        intent.putExtra("MeasureData", (Parcelable) measureData);
        return intent;
    }*/
}