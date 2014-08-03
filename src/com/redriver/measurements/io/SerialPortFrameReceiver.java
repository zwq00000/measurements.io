package com.redriver.measurements.io;

import android.content.Context;
import android.text.TextUtils;
import android_serialport_api.TtySerialPort;
import com.hoho.android.usbserial.SerialPortParameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by zwq00000 on 2014/7/26.
 */
public class SerialPortFrameReceiver extends AbstractFrameReceiver {

    private final Context mContext;
    private TtySerialPort mSerialPort;
    private boolean isClosed;

    public SerialPortFrameReceiver(Context context){
        this.mContext = context;
    }

    private File getPortFile() throws IOException {
            String portName = SerialPortPreferences.getPortName(mContext);
            if(!TextUtils.isEmpty(portName)){
                File portFile = new File(portName);
                if(portFile.exists()){
                    return portFile;
                }
            }
            //没有默认的端口名称
            File portFile = getDefaultPort();
            if(portFile.exists()){
                return portFile;
            }
        return null;
    }

    private static File getDefaultPort(){
        File devFolder = new File("/dev/");
        String[] ttyFiles = devFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith("ttyS");
            }
        });
        if(ttyFiles.length>0) {
            Arrays.sort(ttyFiles);
            return new File(devFolder,ttyFiles[0]);
        }
        throw new NullPointerException("没有找到默认串口");
    }

    private void initSerialPort(File portFile) throws IOException{
        if(portFile == null){
            throw new NullPointerException("port is not been null");
        }
        if(portFile.exists()){
            throw new FileNotFoundException("port " + portFile.getName() +" not found");
        }
        SerialPortParameters parameters = SerialPortPreferences.getParameters(mContext);
        mSerialPort = new TtySerialPort(portFile,parameters.baudRate,0);
    }

    /**
     * 打开连接
     *
     * @return 打开状态
     */
    @Override
    public void open() throws IOException {
        if(mSerialPort !=null){
            throw new IOException("Port is opened");
        }
        isClosed = false;
        initSerialPort(getPortFile());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isClosed){
                    if (mSerialPort != null) {
                        try {
                            ReceivedDataFrameParser.readFromStream(mSerialPort.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                    }else {
                        break;
                    }
                }
                try {
                    SerialPortFrameReceiver.this.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } );
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
            isClosed = true;
            if (mSerialPort != null) {
                mSerialPort.close();
            }
        }finally {
            mSerialPort = null;
        }
    }
}
