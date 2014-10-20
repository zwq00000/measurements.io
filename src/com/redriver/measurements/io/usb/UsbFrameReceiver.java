package com.redriver.measurements.io.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.PatternMatcher;
import android.util.Log;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.AsyncUsbSerialManager;
import com.hoho.android.usbserial.util.Listener;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.FrameReceiver;
import com.redriver.measurements.io.FrameReceiverPreferences;
import com.redriver.measurements.io.ReceivedDataFrameParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Usb BeeFrame 接收器
 * Created by zwq00000 on 2014/7/23.
 */
public class UsbFrameReceiver extends FrameReceiver {
    private static final String TAG = "UsbFrameReceiver";
    private Listener mSerialPortListener = new Listener() {
        @Override
        public void onNewData(ByteBuffer data) {
            try {
                MeasureRecord record = ReceivedDataFrameParser.readFromBytes(data);
                if (record != null && UsbFrameReceiver.this.hasDataReceivedListener()) {
                    UsbFrameReceiver.this.OnReceivedData(record);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, e.getMessage());
            try {
                UsbFrameReceiver.this.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };
    private static final String ACTION_USB_PERMISSION =
            "com.redriver.action.USB_PERMISSION";
    private static final List<String> deviceNameList = new ArrayList<String>();
    /**
     * Usb 串口
     */
    private static com.hoho.android.usbserial.driver.UsbSerialPort sPort;
    /**
     * Usb 串口通讯管理器
     */
    private static SerialInputOutputManager mSerialIoManager;
    /**
     * 轮询服务线程
     */
    private static ExecutorService mExecutor;
    private final UsbManager mUsbManager;
    private final Context mContext;
    private PendingIntent mPermissionIntent;
    private boolean isOpened = false;
    private Future<?> mRunnableFuture;
    private final FrameReceiverPreferences portPreferences;
    /**
     * 打开的设备名称
     */
    private String mDeviceName;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            String deviceName = usbDevice.getDeviceName();

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbSerialDriver serialDriver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
                if (serialDriver != null) {
                    try {
                        UsbFrameReceiver.this.openInternal(serialDriver);
                        deviceNameList.add(deviceName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (deviceName.equals(mDeviceName)) {
                    try {
                        sPort.purgeHwBuffers(true, true);
                        sPort.close();
                        Log.d(TAG, UsbManager.ACTION_USB_DEVICE_DETACHED + " " + deviceName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                boolean permission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,
                        false);
                Log.d(TAG, "ACTION_USB_PERMISSION: " + permission);
                if (permission) {

                }
            }
        }
    };

    public UsbFrameReceiver(Context context, DataReceivedListener receivedListener) {
        this(context);
        this.setDataReceivedListener(receivedListener);
    }

    public UsbFrameReceiver(Context context) {
        super();
        if (context == null) {
            throw new NullPointerException("context is not been null");
        }
        this.mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null) {
            throw new NullPointerException("UsbManager is not been Null");
        }
        registerUsbEventReceiver(context);
        portPreferences = FrameReceiverPreferences.getInstance(context);
    }

    private static UsbSerialDriver getUsbSerialDriver(UsbManager usbManager) {
        List<UsbSerialDriver> drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        if (drivers.size() > 0) {
            return drivers.get(0);
        }
        return null;
    }

    /**
     * 注册 Usb 事件侦听器
     * @param context 应用上下文对象
     */
    private void registerUsbEventReceiver(Context context) {
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addDataScheme("file");
        filter.addDataPath("xml/device_filter", PatternMatcher.PATTERN_SIMPLE_GLOB);
        context.registerReceiver(mUsbReceiver, filter);
    }

    /**
     * 初始化 串口配置
     *
     * @param serialPort
     */
    private void initSerialPort(UsbSerialPort serialPort) throws IOException {
        UsbDeviceConnection connection = mUsbManager.openDevice(serialPort.getDriver().getDevice());
        if (connection == null) {
            throw new IOException("打开 USB 设备 失败");
        }
        AsyncUsbSerialManager manager = new AsyncUsbSerialManager(connection, serialPort);
        manager.setParameters(portPreferences.getParameters());
        manager.open();
        manager.read(mSerialPortListener);
    }

    private void openInternal(UsbSerialDriver usbSerialDriver) throws IOException {
        if (usbSerialDriver == null) {
            return;
        }
        sPort = usbSerialDriver.getPorts().get(0);
        initSerialPort(sPort);
        if (mSerialIoManager != null) {
            mSerialIoManager.stop();
        }
        if (mRunnableFuture != null) {
            mRunnableFuture.cancel(true);
            mRunnableFuture = null;
        }
        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }
    }

    /**
     * 打开连接
     */
    @Override
    public void open() throws IOException {
        if (isOpened) {
            //todo 设备已经打开
            return;
        }
        UsbSerialDriver driver = getUsbSerialDriver(mUsbManager);
        if (driver == null) {
            throw new IOException("没有找到匹配的设备");
        }
        this.mDeviceName = driver.getDevice().getDeviceName();
        openInternal(driver);
        isOpened = true;
    }

    /**
     * Closes the object and release any system resources it holds.
     * <p/>
     * <p>Although only the first call has any effect, it is safe to call close
     * multiple times on the same object. This is more lenient than the
     * overridden {@code AutoCloseable.close()}, which may be called at most
     * once.
     */
    @Override
    public void close() throws IOException {
        try {
            if (sPort != null) {
                sPort.close();
                sPort = null;
            }
            if (mRunnableFuture != null) {
                mRunnableFuture.cancel(true);
                mRunnableFuture = null;
            }
            if (mExecutor != null) {
                mExecutor.shutdown();
                mExecutor = null;
            }
        } finally {
            isOpened = false;
        }
    }

    /**
     * 断开连接，销毁Usb 接收器
     */
    @Override
    public void Terminate() {
        if (this.isOpened) {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                mContext.unregisterReceiver(this.mUsbReceiver);
            }
        }
    }
}
