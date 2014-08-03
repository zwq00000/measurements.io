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

public class SerialPortFinder {

    private static final String TTY_TYPE_SERIAL = "serial";
    private static final String DEVICE_DEFINE_FILE_PATH = "/proc/tty/drivers";
    private static final String TAG = "SerialPort";
    @NotNull
    public static String Tag = "SerialPortFinder";
    @Nullable
    private final Vector<SerialDevice> mDevices;

    public SerialPortFinder() throws IOException {
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

    @NotNull
    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<String>();/**/
            for (SerialDevice device : mDevices) {
                File[] ttyFiles = device.getDevices();
                for (int i = 0; i < ttyFiles.length; i++) {
                    devices.add(ttyFiles[i].getAbsolutePath());
                }
            }
        return devices.toArray(new String[devices.size()]);
    }

    /**
     * 驱动名、缺省的节点名、驱动的主编号、这个驱动使用的次编号范围，tty 驱动的类型
     */
    public class SerialDevice {
        private final String mDriverName;
        private final String mDefaultNode;
        @Nullable
        File[] mDevices = null;

        public SerialDevice(String name, String root) {
            mDriverName = name;
            mDefaultNode = root;
        }


        @Nullable
        public File[] getDevices() {
            if (mDevices == null) {
                File dev = new File("/dev");
                mDevices = dev.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        Log.d(TAG, "filter file " + filename);
                        return filename.contains(mDefaultNode);
                    }
                });
            }
            return mDevices;
        }

        public String getName() {
            return mDriverName;
        }
    }
}
