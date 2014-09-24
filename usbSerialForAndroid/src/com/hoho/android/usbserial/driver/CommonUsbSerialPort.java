/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.hoho.android.usbserial.driver;

import android.annotation.TargetApi;
import android.hardware.usb.*;
import android.os.Build;
import android.util.Log;
import com.hoho.android.usbserial.SerialPortParameters;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A base class shared by several driver implementations.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
abstract class CommonUsbSerialPort implements UsbSerialPort {

    public static final int DEFAULT_READ_BUFFER_SIZE = 4 * 1024;
    public static final int DEFAULT_WRITE_BUFFER_SIZE = 4 * 1024;
    private static final String TAG = "CommonUsbSerialPort";

    protected final UsbDevice mDevice;
    protected final int mPortNumber;
    protected final Object mReadBufferLock = new Object();
    protected final Object mWriteBufferLock = new Object();
    // non-null when open()
    protected UsbDeviceConnection mConnection = null;
    /**
     * read endpoint
     */
    protected UsbEndpoint mReadEndpoint;
    /**
     * write endpoint
     */
    protected UsbEndpoint mWriteEndpoint;

    /**
     * 中断类型 endPoint {@link android.hardware.usb.UsbConstants#USB_ENDPOINT_XFER_INT}
     */
    protected UsbEndpoint mInterruptEndpoint;


    /**
     * Internal read buffer.  Guarded by {@link #mReadBufferLock}.
     */
    protected byte[] mReadBuffer;

    /**
     * Internal write buffer.  Guarded by {@link #mWriteBufferLock}.
     */
    protected byte[] mWriteBuffer;

    public CommonUsbSerialPort(UsbDevice device, int portNumber) {
        mDevice = device;
        mPortNumber = portNumber;
        /*mReadBuffer = new byte[DEFAULT_READ_BUFFER_SIZE];
        mWriteBuffer = new byte[DEFAULT_WRITE_BUFFER_SIZE];*/
    }

    @Override
    public UsbEndpoint getReadEndPoint() {
        return mReadEndpoint;
    }

    @Override
    public UsbEndpoint getWriteEndPoint() {
        return mWriteEndpoint;
    }

    @Override
    public String toString() {
        return String.format("<%s device_name=%s device_id=%s port_number=%s>",
                this.getClass().getSimpleName(), mDevice.getDeviceName(),
                mDevice.getDeviceId(), mPortNumber);
    }

    /**
     * Returns the currently-bound USB device.
     *
     * @return the device
     */
    public final UsbDevice getDevice() {
        return mDevice;
    }

    @Override
    public int getPortNumber() {
        return mPortNumber;
    }

    /**
     * Sets the size of the internal buffer used to exchange data with the USB
     * stack for read operations.  Most users should not need to change this.
     *
     * @param bufferSize the size in bytes
     */
    public final void setReadBufferSize(int bufferSize) {
        synchronized (mReadBufferLock) {
            if (bufferSize == mReadBuffer.length) {
                return;
            }
            mReadBuffer = new byte[bufferSize];
        }
    }

    /**
     * Sets the size of the internal buffer used to exchange data with the USB
     * stack for write operations.  Most users should not need to change this.
     *
     * @param bufferSize the size in bytes
     */
    public final void setWriteBufferSize(int bufferSize) {
        synchronized (mWriteBufferLock) {
            if (bufferSize == mWriteBuffer.length) {
                return;
            }
            mWriteBuffer = new byte[bufferSize];
        }
    }

    @Override
    public void open(UsbDeviceConnection connection) throws IOException {
        if (mConnection != null) {
            //throw new IOException(TAG + " already mOpened.");
            return;
        }

        mConnection = connection;
        boolean opened = false;
        try {
            for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
                UsbInterface usbInterface = mDevice.getInterface(i);
                if (mConnection.claimInterface(usbInterface, true)) {
                    Log.d(TAG, "claimInterface " + i + " SUCCESS");
                } else {
                    Log.d(TAG, "claimInterface " + i + " FAIL");
                }
            }

            UsbInterface usbInterface = mDevice.getInterface(mDevice.getInterfaceCount() - 1);
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint ep = usbInterface.getEndpoint(i);
                int endPointType = ep.getType();
                switch (endPointType) {
                    case UsbConstants.USB_ENDPOINT_XFER_BULK:
                        if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                            mReadEndpoint = ep;
                        } else {
                            mWriteEndpoint = ep;
                        }
                        break;
                    case UsbConstants.USB_ENDPOINT_XFER_INT:
                        Log.d(TAG, "find Interrupt endpoint type " + ep.toString());
                        mInterruptEndpoint = ep;
                        break;
                    case UsbConstants.USB_ENDPOINT_XFER_CONTROL:
                        Log.d(TAG, "find Control endpoint type (endpoint zero) " + ep.toString());
                        break;
                    case UsbConstants.USB_ENDPOINT_XFER_ISOC:
                        Log.d(TAG, "find Isochronous endpoint type (currently not supported) " + ep.toString());
                        break;
                }
            }
            mReadBuffer = new byte[mReadEndpoint.getMaxPacketSize()];
            mWriteBuffer = new byte[mWriteEndpoint.getMaxPacketSize()];
            openInternal();
            opened = true;
        } finally {
            if (!opened) {
                this.close();
            }
        }
    }

    /**
     * 完成打开端口操作之后需要执行的方法
     *
     * @throws IOException
     */
    protected void openInternal() throws IOException {

    }

  /*  @Override
    public abstract void close() throws IOException;*/

    @Override
    public abstract int read(final byte[] dest, final int timeoutMillis) throws IOException;

    /**
     * 读取数据并写入到 {@link java.nio.ByteBuffer} 中
     *
     * @param target
     * @param timeoutMillis
     * @return
     * @throws IOException
     */
    public abstract int read(final ByteBuffer target, final int timeoutMillis) throws IOException;

    protected boolean isOpen() {
        return mConnection != null;
    }

    @Override
    public abstract int write(final byte[] src, final int timeoutMillis) throws IOException;

    @Override
    public abstract void setParameters(
            int baudRate, int dataBits, int stopBits, int parity) throws IOException;

    public void setParameters(SerialPortParameters parameters) throws IOException {
        if (parameters == null) {
            throw new NullPointerException("parameters is not been null");
        }
        setParameters(parameters.baudRate, parameters.dataBits, parameters.stopBits, parameters.parity);
    }

    @Override
    public abstract boolean getCD() throws IOException;

    @Override
    public abstract boolean getCTS() throws IOException;

    @Override
    public abstract boolean getDSR() throws IOException;

    @Override
    public abstract boolean getDTR() throws IOException;

    @Override
    public abstract void setDTR(boolean value) throws IOException;

    @Override
    public abstract boolean getRI() throws IOException;

    @Override
    public abstract boolean getRTS() throws IOException;

    @Override
    public abstract void setRTS(boolean value) throws IOException;

    @Override
    public boolean purgeHwBuffers(boolean flushReadBuffers, boolean flushWriteBuffers) throws IOException {
        return !flushReadBuffers && !flushWriteBuffers;
    }

}
