package com.redriver.measurements.io;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android_serialport_api.SerialPortDevicesFinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 串口配置首选项设置片段
 */
public class SerialPreferencesFragment extends PreferenceFragment {
    private static final String TAG = "SerialPreferencesFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frame_receiver_preferences);
        String portNameKey = getResources().getString(R.string.key_port_name);
        Preference portNamePref = findPreference(FrameReceiverPreferences.KEY_PORT_NAME);
        if (portNamePref != null) {
            if (portNamePref instanceof ListPreference) {
                fillSerialPorts((ListPreference) portNamePref);
            }
        }
    }

    /**
     * 填充 串口列表
     * @param portPreference
     */
    private void fillSerialPorts(ListPreference portPreference) {
        try {
            SerialPortDevicesFinder finder = new SerialPortDevicesFinder();
            List<SerialPortDevicesFinder.SerialDevice> devices = finder.getDevices();
            List<String> fileNames = new ArrayList<String>();
            ArrayList<String> deviceNames = new ArrayList<String>();
            for (SerialPortDevicesFinder.SerialDevice device : devices) {
                File[] ttyFiles = device.getDevices();
                for (int i = 0; i < ttyFiles.length; i++) {
                    String name = ttyFiles[i].getName();
                    String value = String.format("%s (%s)", name, device.getName());
                    deviceNames.add(value);
                    fileNames.add(ttyFiles[i].getAbsolutePath());
                }
            }
            String[] entries = new String[deviceNames.size()];
            String[] entryValues = new String[fileNames.size()];
            portPreference.setEntries(deviceNames.toArray(entries));
            portPreference.setEntryValues(fileNames.toArray(entryValues));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}