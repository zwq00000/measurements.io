package com.redriver.measurements.io.serial;

import android.content.Context;
import android.text.TextUtils;
import android_serialport_api.SerialPort;
import com.hoho.android.usbserial.SerialPortParameters;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.FrameReceiver;
import com.redriver.measurements.io.FrameReceiverPreferences;
import com.redriver.measurements.io.ReceivedDataFrameParser;

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
    private File mPort;
    private SerialPort mSerialPort;
    private Thread mThread;
    private FrameReceiverPreferences portPreferences;

    public SerialPortReceiver(Context context) {
        portPreferences = FrameReceiverPreferences.getInstance(context);
    }

    public SerialPortReceiver(Context context, String portName) throws FileNotFoundException {
        if (!TextUtils.isEmpty(portName)) {
            this.mPort = new File(portName);
            if (!mPort.exists()) {
                throw new FileNotFoundException("port " + portName + " is not found");
            }
        }
    }

    public SerialPortReceiver(File serialPortFile) throws FileNotFoundException {
        if(serialPortFile == null){
            throw new NullPointerException("param serialPortFile is not been Null");
        }
        if(!serialPortFile.exists()){
            throw new FileNotFoundException(serialPortFile.getAbsolutePath());
        }
        this.mPort = serialPortFile;
    }

    /**
     * 获取默认端口文件
     * @return
     */
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

    /**
     * 获取端口文件
     * @return
     * @throws IOException
     */
    private File getPortFile() throws IOException {
        if(mPort!=null){
            return mPort;
        }
        String portName = portPreferences.getPortName();
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

    /**
     * 初始 端口文件
     * @param portFile
     * @throws IOException
     */
    private void initSerialPort(File portFile) throws IOException {
        if (portFile == null) {
            throw new NullPointerException("port is not been null");
        }
        if (!portFile.exists()) {
            throw new FileNotFoundException("port " + portFile.getName() + " not found");
        }
        SerialPortParameters parameters = portPreferences.getParameters();
        mSerialPort = new SerialPort(portFile, parameters.baudRate, 0);
    }

    /**
     * 打开接收器 内部实现的
     *
     * @throws java.io.IOException
     */
    @Override
    protected void openInternal() throws IOException {
        if (mThread != null) {
            return;
        }
        if (mPort == null) {
            mPort = getPortFile();
        }
        initSerialPort(mPort);
        mThread = new RequestThread();
        mThread.start();
    }

    /**
     * 关闭接收器 内部实现
     *
     * @throws java.io.IOException
     */
    @Override
    protected void closeInternal() throws IOException {
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
            while (!isClosed()) {
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
