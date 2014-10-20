package com.redriver.measurements.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.test.AndroidTestCase;
import android.util.Log;
import com.hoho.android.usbserial.SerialPortParameters;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.AsyncUsbSerialManager;
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

    private UsbSerialPort getsPort(Context context) {
        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        assertNotNull(drivers);
        assertTrue(drivers.size() > 0);
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            Log.d(TAG, String.format("+ %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
        }
        return drivers.get(0).getPorts().get(0);
    }

    private UsbSerialPort getPort(UsbManager usbManager) {
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        assertNotNull(drivers);
        assertTrue(drivers.size() > 0);
        //显示Usb设备列表
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            Log.d(TAG, String.format("+ %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
        }
        return drivers.get(0).getPorts().get(0);
    }
    private static final String ACTION_USB_PERMISSION =
            "com.redriver.action.USB_PERMISSION";
    public void testAsyncRead() throws Exception{
        Context context = getContext();

        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        sPort = getPort(usbManager);
        UsbDevice device = sPort.getDriver().getDevice();
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        usbManager.requestPermission(device,mPermissionIntent);
        assertNotNull(usbManager);
        UsbDeviceConnection connection = usbManager.openDevice(device);
        if (connection == null) {
            throw new IOException("打开 USB 设备 失败");
        }
        AsyncUsbSerialManager serialPortManager = new AsyncUsbSerialManager(connection, sPort);
        serialPortManager.setParameters(new SerialPortParameters(115200,8,1,0));
        serialPortManager.open();
        serialPortManager.read(new Listener() {
            @Override
            public void onNewData(ByteBuffer data) {
                assertNotNull(data);
                assertNotNull(data.array());
                Log.d(TAG,"buffer position:"+data.position() + "\tlimit:"+data.limit());
                MeasureRecord record = null;
                try {
                    record = ReceivedDataFrameParser.readFromBytes(data.array());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "gageId:" + record.getGageId() + "\tvalue:" + record.getRawValue());
                assertNotNull(record);
            }

            @Override
            public void onRunError(Exception e) {
                Log.d(TAG, e.toString());
                e.printStackTrace();
            }
        });

        Thread.sleep(1000 * 100);
    }

    public void testConnection() throws Exception {
        Context context = getContext();
        sPort = getsPort(context);
        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        assertNotNull(usbManager);
        UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
        assertNotNull(connection);

        sPort.open(connection);
        sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

        showMessage("Serial device: " + sPort.getClass().getSimpleName());

        //sPort.setParameters(115200, 8, 1, 0);

        mSerialIoManager = new SerialInputOutputManager(sPort, new Listener() {
            @Override
            public void onNewData(ByteBuffer data) {
                Log.d(TAG,"SerialInputOutputManager onNewData");
                assertNotNull(data);
                assertNotNull(data.array());
                Log.d(TAG,"buffer position:"+data.position() + "\tlimit:"+data.limit());
                MeasureRecord record = null;
                try {
                    record = ReceivedDataFrameParser.readFromBytes(data.array());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "gageId:" + record.getGageId() + "\tvalue:" + record.getRawValue());
                assertNotNull(record);
            }

            @Override
            public void onRunError(Exception e) {
                Log.d(TAG, e.toString());
                e.printStackTrace();
            }
        });
        //mExecutor.submit(mSerialIoManager);
        //new Thread(mSerialIoManager).start();
        for(int i=0;i<100;i++){
          Log.d(TAG,"mSerialIoManager.run " + i);
          mSerialIoManager.run();
          //Thread.sleep(1000);
        }

        Thread.sleep(1000 * 100);
    }


    /**
     * 显示 Toast 消息
     *
     * @param msg
     */
    private void showMessage(final String msg) {
        Log.d(TAG, msg);
    }
}
