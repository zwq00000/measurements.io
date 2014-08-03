package android_serialport_api.test;

import android.test.AndroidTestCase;
import android_serialport_api.SerialPortFinder;

import java.util.List;

public class SerialPortFinderTest extends AndroidTestCase {

    public void testGetDevices() throws Exception {
        SerialPortFinder finder = new SerialPortFinder();
        List<SerialPortFinder.SerialDevice> devices = finder.getDevices();
        assertNotNull(devices);
        assertTrue(devices.size()>0);
        for(SerialPortFinder.SerialDevice device :devices){
            System.out.println(device.getName());
        }
    }

    public void testGetAllDevices() throws Exception {
        SerialPortFinder finder = new SerialPortFinder();
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