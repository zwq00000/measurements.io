package com.redriver.measurements.io.test;

import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.BeeFrame;
import com.redriver.measurements.io.serial.InputStreamReceiver;
import junit.framework.TestCase;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created by zwq00000 on 2014/5/25.
 */
public class BeeIFrameReceiverTest extends TestCase {
    
    public void testConstructer() throws IOException, InterruptedException {

        PipedInputStream inputStream = new PipedInputStream();
        InputStreamReceiver reciver = new InputStreamReceiver(inputStream);
        reciver.setDataReceivedListener(new InputStreamReceiver.DataReceivedListener() {
            @Override
            public void onDataReceived(MeasureRecord event) {
                System.out.println(event.getGageId());
                assertEquals(event.getGageId(), "0001");
            }
        });
        new Thread(reciver).start();
        OutputStream outputStream = new PipedOutputStream(inputStream);
        for (int i=0;i<10;i++){
            BeeFrame frame = TestUtils.genReceiveBeeFrame("0001", new Integer(i).toString());
            outputStream.write(frame.getBytes());
            outputStream.flush();
            Thread.sleep(100);
        }
    }
}
