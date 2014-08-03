package com.redriver.measurements.usb;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.test.AndroidTestCase;
import android.util.Log;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.Listener;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.ReceivedDataFrameParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zwq00000 on 2014/7/23.
 */
public class UsbSerialTest extends AndroidTestCase {
    private static final String TAG = "UsbSerialTest";
    private static UsbSerialPort sPort = null;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

    private UsbSerialPort getsPort(Context context){
        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        assertNotNull(drivers);
        assertTrue(drivers.size()>0);
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            Log.d(TAG, String.format("+ %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
        }
        return drivers.get(0).getPorts().get(0);
    }

    public void testConnection() throws Exception{
        Context context = getContext();
        sPort = getsPort(context);
        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
        if (connection == null) {
            showMessage("Opening device failed");
            return;
        }
        try {
            sPort.open(connection);
            sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            showMessage("Error opening device: " + e.getMessage());
            try {
                sPort.close();
            } catch (IOException e2) {
                // Ignore.
            }
            sPort = null;
            return;
        }
        showMessage("Serial device: " + sPort.getClass().getSimpleName());

        sPort.setParameters(115200,8,1,0);

        mSerialIoManager = new SerialInputOutputManager(sPort, new Listener() {
            @Override
            public void onNewData(ByteBuffer data) {
                try {
                    MeasureRecord record = ReceivedDataFrameParser.readFromBytes(data.array());
                    assertNotNull(record);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRunError(Exception e) {
                 Log.d(TAG,e.getMessage());
            }
        });
        mExecutor.submit(mSerialIoManager);

        Thread.sleep(1000*100);
    }


    /**
     * 显示 Toast 消息
     *
     * @param msg
     */
    private void showMessage(final String msg) {
            Log.d(TAG,msg);
    }
}
