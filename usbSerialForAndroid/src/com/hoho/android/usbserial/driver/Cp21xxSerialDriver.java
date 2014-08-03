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
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbRequest;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class Cp21xxSerialDriver implements UsbSerialDriver {

    private static final String TAG = "Cp21xxSerialDriver";

    private final UsbDevice mDevice;
    private final UsbSerialPort mPort;

    public Cp21xxSerialDriver(UsbDevice device) {
        mDevice = device;
        mPort = new Cp21xxSerialPort(mDevice, 0);
    }

    @Override
    public UsbDevice getDevice() {
        return mDevice;
    }

    @Override
    public List<UsbSerialPort> getPorts() {
        return Collections.singletonList(mPort);
    }

    public class Cp21xxSerialPort extends CommonUsbSerialPort {

        private static final int DEFAULT_BAUD_RATE = 9600;

        private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;

        /*
         * Configuration Request Types
         */
        private static final int REQTYPE_HOST_TO_DEVICE = 0x41;

        /*
         * Configuration Request Codes
         */
        private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0x00;
        private static final int SILABSER_SET_BAUDDIV_REQUEST_CODE = 0x01;
        private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 0x03;
        private static final int SILABSER_SET_MHS_REQUEST_CODE = 0x07;
        private static final int SILABSER_SET_BAUDRATE = 0x1E;
        private static final int SILABSER_FLUSH_REQUEST_CODE = 0x12;

        private static final int FLUSH_READ_CODE = 0x0a;
        private static final int FLUSH_WRITE_CODE = 0x05;

        /*
         * SILABSER_IFC_ENABLE_REQUEST_CODE
         */
        private static final int UART_ENABLE = 0x0001;
        private static final int UART_DISABLE = 0x0000;

        /*
         * SILABSER_SET_BAUDDIV_REQUEST_CODE
         */
        private static final int BAUD_RATE_GEN_FREQ = 0x384000;

        /*
         * SILABSER_SET_MHS_REQUEST_CODE
         */
        private static final int MCR_DTR = 0x0001;
        private static final int MCR_RTS = 0x0002;
        private static final int MCR_ALL = 0x0003;

        private static final int CONTROL_WRITE_DTR = 0x0100;
        private static final int CONTROL_WRITE_RTS = 0x0200;

        private static final int BITS_DATA_MASK = 0X0f00;
        private static final int BITS_DATA_5 = 0X0500;
        private static final int BITS_DATA_6 = 0X0600;
        private static final int BITS_DATA_7 = 0X0700;
        private static final int BITS_DATA_8 = 0X0800;
        private static final int BITS_DATA_9 = 0X0900;

        private static final int BITS_PARITY_MASK = 0X00f0;
        private static final int BITS_PARITY_NONE = 0X0000;
        private static final int BITS_PARITY_ODD = 0X0010;
        private static final int BITS_PARITY_EVEN = 0X0020;
        private static final int BITS_PARITY_MARK = 0X0030;
        private static final int BITS_PARITY_SPACE = 0X0040;

        private static final int BITS_STOP_MASK = 0X000f;
        private static final int BITS_STOP_1 = 0X0000;
        private static final int BITS_STOP_1_5 = 0X0001;
        private static final int BITS_STOP_2 = 0X0002;

        public Cp21xxSerialPort(UsbDevice device, int portNumber) {
            super(device, portNumber);
        }

        @Override
        public UsbSerialDriver getDriver() {
            return Cp21xxSerialDriver.this;
        }

        private int setConfigSingle(int request, int value) {
            return mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, request, value,
                    0, null, 0, USB_WRITE_TIMEOUT_MILLIS);
        }

        private void checkOpen() throws IOException {
            if (!isOpen()) {
                throw new IOException("连接已经关闭");
            }
        }

        @Override
        protected void openInternal() throws IOException{
            setConfigSingle(SILABSER_IFC_ENABLE_REQUEST_CODE, UART_ENABLE);
            setConfigSingle(SILABSER_SET_MHS_REQUEST_CODE, MCR_ALL | CONTROL_WRITE_DTR | CONTROL_WRITE_RTS);
            setConfigSingle(SILABSER_SET_BAUDDIV_REQUEST_CODE, BAUD_RATE_GEN_FREQ / DEFAULT_BAUD_RATE);
            //            setParameters(DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_PARITY);
        }

        @Override
        public void close() throws IOException {
            if (mConnection == null) {
                throw new IOException("Already closed");
            }
            try {
                setConfigSingle(SILABSER_IFC_ENABLE_REQUEST_CODE, UART_DISABLE);
                mConnection.close();
            } finally {
                mConnection = null;
            }
        }

        @Override
        public int read(final byte[] dest, int timeoutMillis) throws IOException {
            checkOpen();
            if (dest.length < mReadEndpoint.getMaxPacketSize()) {
                throw new UsbSerialRuntimeException("dest array size not less then " + mReadEndpoint.getMaxPacketSize());
            }
            final int numBytesRead;
            synchronized (mReadBufferLock) {
                int readAmt = dest.length;
                numBytesRead = mConnection.bulkTransfer(mReadEndpoint, dest, readAmt,
                        timeoutMillis);
                if (numBytesRead < 0) {
                    // This sucks: we get -1 on timeout, not 0 as preferred.
                    // We *should* use UsbRequest, except it has a bug/api oversight
                    // where there is no way to determine the number of bytes read
                    // in response :\ -- http://b.android.com/28023
                    return 0;
                }
            }
            return numBytesRead;
        }

        @Override
        public int read(final ByteBuffer buffer, int timeoutMillis) throws IOException {
            checkOpen();
            int readAmt = buffer.remaining();
            readAmt = Math.min(readAmt, mReadEndpoint.getMaxPacketSize());
            final UsbRequest request = new UsbRequest();
            try {
                request.initialize(mConnection, this.mReadEndpoint);
                if (!request.queue(buffer, readAmt)) {
                    throw new IOException("Error queueing request.");
                }
                if (mConnection.requestWait() == request) {
                    if(buffer.position()==0) {
                        buffer.position(readAmt);
                    }
                    return readAmt;
                }
                return 0;
            } finally {
                request.close();
            }
        }

        @Override
        public int write(byte[] src, int timeoutMillis) throws IOException {
            checkOpen();
            int offset = 0;

            while (offset < src.length) {
                final int writeLength;
                final int amtWritten;

                synchronized (mWriteBufferLock) {
                    final byte[] writeBuffer;

                    writeLength = Math.min(src.length - offset, mWriteBuffer.length);
                    if (offset == 0) {
                        writeBuffer = src;
                    } else {
                        // bulkTransfer does not support offsets, make a copy.
                        System.arraycopy(src, offset, mWriteBuffer, 0, writeLength);
                        writeBuffer = mWriteBuffer;
                    }

                    amtWritten = mConnection.bulkTransfer(mWriteEndpoint, writeBuffer, writeLength,
                            timeoutMillis);
                }
                if (amtWritten <= 0) {
                    throw new IOException("Error writing " + writeLength
                            + " bytes at offset " + offset + " length=" + src.length);
                }

                Log.d(TAG, "Wrote amt=" + amtWritten + " attempted=" + writeLength);
                offset += amtWritten;
            }
            return offset;
        }

        private void setBaudRate(int baudRate) throws IOException {
            byte[] data = new byte[]{
                    (byte) (baudRate & 0xff),
                    (byte) ((baudRate >> 8) & 0xff),
                    (byte) ((baudRate >> 16) & 0xff),
                    (byte) ((baudRate >> 24) & 0xff)
            };
            int ret = mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, SILABSER_SET_BAUDRATE,
                    0, 0, data, 4, USB_WRITE_TIMEOUT_MILLIS);
            if (ret < 0) {
                throw new IOException("Error setting baud rate.");
            }
        }

        @Override
        public void setParameters(int baudRate, int dataBits, int stopBits, int parity)
                throws IOException {
            setBaudRate(baudRate);

            int configDataBits = 0;
            switch (dataBits) {
                case DATABITS_5:
                    configDataBits |= BITS_DATA_5;
                    break;
                case DATABITS_6:
                    configDataBits |= BITS_DATA_6;
                    break;
                case DATABITS_7:
                    configDataBits |= BITS_DATA_7;
                    break;
                case DATABITS_8:
                    configDataBits |= BITS_DATA_8;
                    break;
                default:
                    configDataBits |= BITS_DATA_8;
                    break;
            }
            setConfigSingle(SILABSER_SET_LINE_CTL_REQUEST_CODE, configDataBits);

            int configParityBits = 0; // PARITY_NONE
            switch (parity) {
                case PARITY_ODD:
                    configParityBits |= BITS_PARITY_ODD;
                    break;
                case PARITY_EVEN:
                    configParityBits |= BITS_PARITY_EVEN;
                    break;
            }
            setConfigSingle(SILABSER_SET_LINE_CTL_REQUEST_CODE, configParityBits);

            int configStopBits = 0;
            switch (stopBits) {
                case STOPBITS_1:
                    configStopBits |= BITS_STOP_1;
                    break;
                case STOPBITS_2:
                    configStopBits |= BITS_STOP_2;
                    break;
            }
            setConfigSingle(SILABSER_SET_LINE_CTL_REQUEST_CODE, configStopBits);
        }

        @Override
        public boolean getCD() throws IOException {
            return false;
        }

        @Override
        public boolean getCTS() throws IOException {
            return false;
        }

        @Override
        public boolean getDSR() throws IOException {
            return false;
        }

        @Override
        public boolean getDTR() throws IOException {
            return true;
        }

        @Override
        public void setDTR(boolean value) throws IOException {
        }

        @Override
        public boolean getRI() throws IOException {
            return false;
        }

        @Override
        public boolean getRTS() throws IOException {
            return true;
        }

        @Override
        public void setRTS(boolean value) throws IOException {
        }

        @Override
        public boolean purgeHwBuffers(boolean purgeReadBuffers,
                                      boolean purgeWriteBuffers) throws IOException {
            int value = (purgeReadBuffers ? FLUSH_READ_CODE : 0)
                    | (purgeWriteBuffers ? FLUSH_WRITE_CODE : 0);

            if (value != 0) {
                setConfigSingle(SILABSER_FLUSH_REQUEST_CODE, value);
            }

            return true;
        }

    }

    public static Map<Integer, int[]> getSupportedDevices() {
        final Map<Integer, int[]> supportedDevices = new LinkedHashMap<Integer, int[]>();
        supportedDevices.put(Integer.valueOf(UsbId.VENDOR_SILABS),
                new int[]{
                        UsbId.SILABS_CP2102,
                        UsbId.SILABS_CP2105,
                        UsbId.SILABS_CP2108,
                        UsbId.SILABS_CP2110
                }
        );
        return supportedDevices;
    }

}
