/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 串口设备查找器
 */
public class SerialPortDevicesFinder {

    /**
     * 串口设备类型名称
     */
    private static final String TTY_TYPE_SERIAL = "serial";
    /**
     * tty 设备定义文件
     */
    private static final String DEVICE_DEFINE_FILE_PATH = "/proc/tty/drivers";
    private static final String TAG = "SerialPort";
    @Nullable
    private final Vector<SerialDevice> mDevices;

    public SerialPortDevicesFinder() throws IOException {
        mDevices = new Vector<SerialDevice>();
        initDevices();
    }

    public List<SerialDevice> getDevices(){
        return mDevices;
    }

    @Nullable
    private List<SerialDevice> initDevices() throws IOException {
        if (mDevices.isEmpty()) {
            //文件结构
            //驱动名、缺省的节点名、驱动的主编号、这个驱动使用的次编号范围，tty 驱动的类型
            LineNumberReader reader = new LineNumberReader(new FileReader(DEVICE_DEFINE_FILE_PATH));
            try {
                String l;
                while ((l = reader.readLine()) != null) {
                    // Issue 3:
                    // Since driver name may contain spaces, we do not extract driver name with split()
                    String deviceName = l.substring(0, 0x15).trim();
                    String[] w = l.split(" +");
                    if ((w.length >= 5) && (w[w.length - 1].equals(TTY_TYPE_SERIAL))) {
                        Log.d(TAG, "Found new driver " + deviceName + " on " + w[w.length - 4]);
                        mDevices.add(new SerialDevice(deviceName, w[w.length - 4]));
                    }
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        return mDevices;
    }

    @NotNull
    public String[] getAllDevices() {
        ArrayList<String> devices = new ArrayList<String>();
        for (SerialDevice device : mDevices) {
            File[] ttyFiles = device.getDevices();
            for (int i = 0; i < ttyFiles.length; i++) {
                String name = ttyFiles[i].getName();
                String value = String.format("%s (%s)", name, device.getName());
                devices.add(value);
            }
        }
        return devices.toArray(new String[devices.size()]);
    }

    /**
     * 驱动名、缺省的节点名、驱动的主编号、这个驱动使用的次编号范围，tty 驱动的类型
     */
    public class SerialDevice implements FilenameFilter {
        private final String mDriverName;
        private final String mFileNamePattern;
        private final File mDevFile;
        @Nullable
        File[] mDevices = null;

        public SerialDevice(String driverName, String devFileName) {
            mDriverName = driverName;
            File patternFile = new File(devFileName);
            mFileNamePattern = patternFile.getName();
            mDevFile = patternFile.getParentFile();
        }

        /**
         * 获取设备文件列表
         * @return
         */
        @Nullable
        public File[] getDevices() {
            if (mDevices == null) {
                mDevices = mDevFile.listFiles(this);
            }
            return mDevices;
        }

        /**
         * 设备名称
         * @return
         */
        public String getName() {
            return mDriverName;
        }

        /**
         * Indicates if a specific filename matches this filter.
         *
         * @param dir      the directory in which the {@code filename} was found.
         * @param filename the name of the file in {@code dir} to test.
         * @return {@code true} if the filename matches the filter
         * and can be included in the list, {@code false}
         * otherwise.
         */
        @Override
        public boolean accept(File dir, String filename) {
            return dir.equals(this.mDevFile) && filename.startsWith(mFileNamePattern);
        }
    }
}
