package com.hoho.android.usbserial.util;

import android.annotation.TargetApi;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbRequest;
import android.os.Build;
import com.hoho.android.usbserial.SerialPortParameters;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zwq00000 on 2014/7/26.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class AsyncUsbSerialManager {
    private static final String TAG = "AsyncUsbSerialManager";
    private final UsbSerialPort mDriver;
    private final UsbDeviceConnection mConnection;
    private final ByteBuffer mReadBuffer;
    private final UsbRequest mReadRequest;
    private RequestThread mThread;
    private volatile boolean isClosed;
    /*private final ArrayList<Listener> mCallbackListeners;*/
    private Listener mReadListener;
    private SerialPortParameters portParameters;

    /**
     * Creates a new instance with no listener.
     */
    public AsyncUsbSerialManager(UsbDeviceConnection connection, UsbSerialPort driver) {
        if (connection == null) {
            throw new NullPointerException("usb device connection is not been null");
        }
        if (driver == null) {
            throw new NullPointerException("usb serial port driver is not been null");
        }
        this.mConnection = connection;
        mDriver = driver;
        mReadBuffer = ByteBuffer.allocate(1024);
        mReadRequest = new UsbRequest();
        isClosed = false;
    }

    /**
     * Sets various serial port parameters.
     *
     * @param settings
     * @throws IOException
     */
    public void setParameters(SerialPortParameters settings) throws IOException {
        portParameters = settings;
    }

    public void open() throws IOException {
        if (mThread != null) {
            return;
        }
        mDriver.open(mConnection);
        if (portParameters != null) {
            mDriver.setParameters(portParameters);
        } else {
            mDriver.setParameters(SerialPortParameters.DefaultSettings);
        }
        mReadRequest.initialize(mConnection, mDriver.getReadEndPoint());
        mThread = new RequestThread();
        mThread.start();
    }

    public void close() {
        isClosed = true;
        if (mReadRequest != null) {
            mReadRequest.cancel();
            mReadRequest.close();
        }
    }

    private void checkClosed() throws IOException {
        if (isClosed) {
            throw new IOException("Usb connection is closed");
        }
    }

    public void write(byte[] data) throws IOException {
        checkClosed();
        UsbRequest writeRequest = new UsbRequest();
        writeRequest.initialize(this.mConnection, this.mDriver.getWriteEndPoint());
        ByteBuffer buffer = ByteBuffer.wrap(data);
        if (!writeRequest.queue(buffer, data.length)) {
            throw new IOException("error queueing request.");
        }
    }

    public void read(final Listener callback) {
        if (callback == null) {
            throw new NullPointerException("callback listener is not been null");
        }
        this.mReadListener = callback;
    }

    private final class RequestThread extends java.lang.Thread {
        RequestThread() {
            super("UsbSerial-" + mDriver.getDriver().getDevice().getDeviceName());
        }

        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run() {
            if (mConnection != null) {
                try {
                    while (!isClosed) {
                        mReadBuffer.clear();
                        mReadRequest.queue(mReadBuffer, mReadBuffer.remaining());
                        UsbRequest request = mConnection.requestWait();
                        if (request == mReadRequest) {
                            if (mReadBuffer.position() == 0) {
                                mReadBuffer.position(request.getEndpoint().getMaxPacketSize());
                            }
                            mReadBuffer.flip();
                            if (mReadListener != null) {
                                mReadListener.onNewData(mReadBuffer);
                            }
                        } else {
                            //todo Usb 连接断开
                            //return;
                        }
                    }
                } finally {
                    mThread = null;
                }
            }
        }
    }
}
