package com.redriver.measurements.io.test;

import com.redriver.measurements.io.FrameTypes;
import junit.framework.TestCase;

public class FrameTypesTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testToDescription() throws Exception {
        assertFrameType(FrameTypes.ECHO);
        assertFrameType(FrameTypes.FACTORY_RESET);
        assertFrameType(FrameTypes.READ_DEVICE_KEY);
        assertFrameType(FrameTypes.READ_RECEIVER_INFO);
        assertFrameType(FrameTypes.READ_RECEIVER_KEY);
        assertFrameType(FrameTypes.SET_RECEIVER_KEY);
        assertFrameType((byte) (FrameTypes.RX_FRAME | FrameTypes.RECEIVED_DATA));
    }

    private static void assertFrameType(byte frameType){
        assertNotNull(FrameTypes.toDescription(frameType));
        System.out.println(FrameTypes.toDescription(frameType));
    }
}