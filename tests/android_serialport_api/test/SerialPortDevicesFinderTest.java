package android_serialport_api.test;

import android.test.AndroidTestCase;
import android.util.Log;
import android_serialport_api.SerialPortDevicesFinder;

import java.io.File;
import java.util.List;

public class SerialPortDevicesFinderTest extends AndroidTestCase {

    public void testGetDevices() throws Exception {
        SerialPortDevicesFinder finder = new SerialPortDevicesFinder();
        List<SerialPortDevicesFinder.SerialDevice> devices = finder.getDevices();
        assertNotNull(devices);
        assertTrue(devices.size()>0);
        for(SerialPortDevicesFinder.SerialDevice device :devices){
            Log.d("SerialPort", device.getName() + "\tcount:" + device.getDevices().length);
            for(File deviceFile : device.getDevices()){
                Log.d("SerialPort", "\tdevice file:" + deviceFile.getAbsolutePath());
            }
        }
    }

    public void testGetAllDevices() throws Exception {
        SerialPortDevicesFinder finder = new SerialPortDevicesFinder();
        String[] devices = finder.getAllDevices();
        assertNotNull(devices);
        assertTrue(devices.length > 0);
        for (int i = 0; i < devices.length; i++) {
            System.out.println(devices[i]);
        }
    }

    public void testGetAllDevicesPath() throws Exception {

    }
}