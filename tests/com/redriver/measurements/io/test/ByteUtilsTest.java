package com.redriver.measurements.io.test;

import com.redriver.measurements.io.ByteUtils;
import junit.framework.TestCase;

public class ByteUtilsTest extends TestCase {
    public void testToByteArray() throws Exception {
        byte[] testBytes = new byte[]{0x0,0x0,0x0,0x0};
        int testInt = 0;
        TestUtils.assertArrayEquals(ByteUtils.toByteArray(testInt), testBytes);

        testBytes = new byte[]{0x8,0x4,0x2,0x1};
        testInt = 0x08040201;
        TestUtils.assertArrayEquals(ByteUtils.toByteArray(testInt), testBytes);
    }

    public void testToByteArray1() throws Exception {
        byte[] testBytes = new byte[]{0x0,0x0};
        short testShort = 0;
        TestUtils.assertArrayEquals(ByteUtils.toByteArray(testShort), testBytes);

        testBytes = new byte[]{0x2,0x1};
        testShort = 0x0201;
        TestUtils.assertArrayEquals(ByteUtils.toByteArray(testShort), testBytes);
    }

    public void testBytesToInt() throws Exception {
        System.out.println("setUp Test ByteUtils.bytesToInt");
        byte[] testBytes = new byte[]{0x0,0x0,0x0,0x0};
        int testInt = 0;
        assertEquals(ByteUtils.bytesToInt(testBytes), testInt);

        testBytes = new byte[]{0x8,0x4,0x2,0x1};
        testInt = 0x08040201;
        assertEquals(ByteUtils.bytesToInt(testBytes), testInt);
    }

    public void testBytesToShort() throws Exception {
        byte[] testBytes = new byte[]{0x0,0x0};
        short testShort = 0;
        assertEquals(ByteUtils.bytesToShort(testBytes), testShort);

        testBytes = new byte[]{0x2,0x1};
        testShort = 0x0201;
        assertEquals(ByteUtils.bytesToShort(testBytes), testShort);
    }

    public void testBytesToShort1() throws Exception {
        byte[] testBytes = new byte[]{0x0,0x0};
        short testShort = 0;
        assertEquals(ByteUtils.bytesToShort(testBytes,0), testShort);

        testBytes = new byte[]{0x8,0x4,0x2,0x1};
        testShort = 0x0201;
        assertEquals(ByteUtils.bytesToShort(testBytes,2), testShort);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }
}