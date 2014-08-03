package com.redriver.measurements.io.test;

import android.content.Context;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Created by zwq00000 on 2014/7/26.
 */
public class SerialPortTest extends AndroidTestCase {

    public void testPortList() throws Exception{
        Context context = getContext();
        File devFolder = new File("/dev/");
        String[] ttyFiles = devFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith("ttyS");
            }
        });
        assertNotNull(ttyFiles);
        assertTrue(ttyFiles.length>0);
        Arrays.sort(ttyFiles);
        for (int i=0;i<ttyFiles.length;i++){
            System.out.println(ttyFiles[i]);
        }

        assertEquals(ttyFiles[0],"ttySAC0");

    }
}
