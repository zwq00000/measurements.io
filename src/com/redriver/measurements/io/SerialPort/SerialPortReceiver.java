package com.redriver.measurements.io.SerialPort;

import android.content.Context;
import android.text.TextUtils;
import android_serialport_api.SerialPort;
import com.hoho.android.usbserial.SerialPortParameters;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.FrameReceiver;
import com.redriver.measurements.io.ReceivedDataFrameParser;
import com.redriver.measurements.io.SerialPortPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * 串口数据接收器
 * Created by zwq00000 on 2014/7/26.
 */
public class SerialPortReceiver extends FrameReceiver {

    private static final String TAG = "SerialPortReceiver";
    private final Context mContext;
    private File mPort;
    private SerialPort mSerialPort;
    private boolean isClosed;
    private Thread mThread;

    public SerialPortReceiver(Context context) {
        this.mContext = context;
    }

    public SerialPortReceiver(Context context, String portName) throws FileNotFoundException {
        this.mContext = context;
        if (!TextUtils.isEmpty(portName)) {
            this.mPort = new File(portName);
            if (!mPort.exists()) {
                throw new FileNotFoundException("port " + portName + " is not found");
            }
        }
    }

    private static File getDefaultPort() {
        File devFolder = new File("/dev/");
        String[] ttyFiles = devFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith("ttyS");
            }
        });
        if (ttyFiles.length > 0) {
            Arrays.sort(ttyFiles);
            return new File(devFolder, ttyFiles[0]);
        }
        throw new NullPointerException("没有找到默认串口");
    }

    private File getPortFile() throws IOException {
        String portName = SerialPortPreferences.getPortName(mContext);
        if (!TextUtils.isEmpty(portName)) {
            File portFile = new File(portName);
            if (portFile.exists()) {
                return portFile;
            }
        }
        //没有默认的端口名称
        File portFile = getDefaultPort();
        if (portFile.exists()) {
            return portFile;
        }
        return null;
    }

    private void initSerialPort(File portFile) throws IOException {
        if (portFile == null) {
            throw new NullPointerException("port is not been null");
        }
        if (!portFile.exists()) {
            throw new FileNotFoundException("port " + portFile.getName() + " not found");
        }
        SerialPortParameters parameters = SerialPortPreferences.getParameters(mContext);
        mSerialPort = new SerialPort(portFile, parameters.baudRate, 0);
    }

    /**
     * 打开连接
     *
     * @return 打开状态
     */
    @Override
    public void open() throws IOException {
        if (mThread != null) {
            return;
        }
        isClosed = false;
        if (mPort == null) {
            mPort = getPortFile();
        }
        initSerialPort(mPort);
        mThread = new RequestThread();
        mThread.start();
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
        isClosed = true;
        if (mSerialPort != null) {
            mSerialPort.getInputStream().close();
            mSerialPort.close();
        }
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private final class RequestThread extends java.lang.Thread {
        RequestThread() {
            super(SerialPortReceiver.TAG + mSerialPort.getPortName());
        }

        @Override
        public void run() {
            while (!isClosed) {
                if (mSerialPort != null) {
                    try {
                        MeasureRecord record = ReceivedDataFrameParser.readFromStream(mSerialPort.getInputStream());
                        if (record != null && hasDataReceivedListener()) {
                            OnReceivedData(record);
                        }
                        record.recycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } else {
                    break;
                }
            }
            try {
                if (mSerialPort != null) {
                    mSerialPort.close();
                }
            } finally {
                mThread = null;
            }
        }
    }
}
