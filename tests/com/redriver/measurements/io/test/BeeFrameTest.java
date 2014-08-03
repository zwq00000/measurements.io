package com.redriver.measurements.io.test;

import android.annotation.TargetApi;
import android.os.Build;
import com.redriver.measurements.io.BeeFrame;
import com.redriver.measurements.io.FrameTypes;
import junit.framework.TestCase;

import java.nio.charset.Charset;

public class BeeFrameTest extends TestCase {

    public void testGetData() throws Exception {

    }

    public void testGetDataLen() throws Exception {
        BeeFrame frame = new BeeFrame((byte)(FrameTypes.RX_FRAME |FrameTypes.RECEIVED_DATA));
        assertEquals(frame.getDataLen(),1);

        frame = new BeeFrame(FrameTypes.RECEIVED_DATA,new byte[10]);
        assertEquals(frame.getDataLen(),10+1);
    }

    public void testGetFrameType() throws Exception {

    }

    public void testGetBaseFrameType() throws Exception {
        BeeFrame frame = new BeeFrame((byte)(FrameTypes.RX_FRAME |FrameTypes.RECEIVED_DATA));
        assertEquals(frame.getBaseFrameType(),FrameTypes.RECEIVED_DATA);

        frame = new BeeFrame(FrameTypes.RECEIVED_DATA);
        assertEquals(frame.getBaseFrameType(),FrameTypes.RECEIVED_DATA);
    }

    public void testIsRxFrame() throws Exception {
        BeeFrame frame = new BeeFrame((byte)(FrameTypes.RX_FRAME |FrameTypes.RECEIVED_DATA));
        assertTrue(frame.isRxFrame());

        frame = new BeeFrame(FrameTypes.RECEIVED_DATA);
        assertFalse(frame.isRxFrame());
    }

    public void testGetCheckSum() throws Exception {

    }

    public void testWriteTo() throws Exception {

    }

    public void testToBytes() throws Exception {
        String receiveMsg = "ID:0003 12.123\r\n";
        byte[] data = receiveMsg.getBytes() ;
        BeeFrame frame = TestUtils.genReceiveBeeFrame(receiveMsg);

        byte[]  bytes = frame.getBytes();
        TestUtils.assertArrayEquals(bytes, TestUtils.FrameBytes);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void testToBytes1() throws Exception{
        BeeFrame txEcho = new BeeFrame(FrameTypes.ECHO);
        byte[] bytes = new byte[]{
                (byte) 0xFE, 0x01, 0x3F, 0x3F
        };
        TestUtils.assertArrayEquals(txEcho.getBytes(), bytes);

        BeeFrame rxEcho = new BeeFrame((byte)( FrameTypes.ECHO | FrameTypes.RX_FRAME), new byte[]{0, 0,(byte) 0xD0});
        bytes = new byte[]{(byte)0xFE, 0x04, (byte)0xBF, 0x00, 0x00,(byte) 0xD0, 0x6F};
        TestUtils.assertArrayEquals(rxEcho.getBytes(), bytes);

        BeeFrame txReadReciverInfo = new BeeFrame(FrameTypes.READ_RECEIVER_INFO);
        bytes = new byte[]{(byte)0xFE, 0x01, 0x31, 0x31};
        TestUtils.assertArrayEquals(txReadReciverInfo.getBytes(), bytes);

        String reciverInfo = "Center \r\nHw rev:10\r\nSw rev:10\r\nDevelop by sxl\r\nDAT:13-10\r\n";
        byte[] reciverInfoBytes =reciverInfo.getBytes();
        byte[] data = new byte[reciverInfoBytes.length+2];
        data[0] = 1;
        System.arraycopy(reciverInfoBytes,0,data,1,reciverInfoBytes.length);
        data[data.length-1] = 0;
        BeeFrame rxReadReciverInfo = new BeeFrame((byte)(FrameTypes.READ_RECEIVER_INFO | FrameTypes.RX_FRAME),data);
        bytes = new byte[]{
                (byte)0xFE, 0x3D, (byte)0xB1, 0x01, 0x43, 0x65, 0x6E, 0x74, 0x65, 0x72, 0x20, 0x0D, 0x0A, 0x48,
                0x77, 0x20, 0x72, 0x65, 0x76, 0x3A, 0x31, 0x30, 0x0D, 0x0A, 0x53,
                0x77, 0x20, 0x72, 0x65, 0x76, 0x3A, 0x31, 0x30, 0x0D, 0x0A, 0x44, 0x65, 0x76, 0x65,
                0x6C, 0x6F, 0x70, 0x20, 0x62, 0x79, 0x20, 0x73, 0x78, 0x6C, 0x0D,
                0x0A, 0x44, 0x41, 0x54, 0x3A, 0x31, 0x33, 0x2D, 0x31, 0x30, 0x0D, 0x0A, 0x00, (byte)0xDF
        };

        TestUtils.assertArrayEquals(rxReadReciverInfo.getBytes(), bytes);

        String valueStr = "ID:3637 6.62\r\n";
        byte[] valueStrBytes = valueStr.getBytes(Charset.forName("US-ASCII"));
        data = new byte[valueStrBytes.length+5];
        data[0] = 0x00;
        data[1] = 0x0E;
        data[2] = 0x0E;
        data[3] = 0x4f;
        data[4] = 0x07;
        System.arraycopy(valueStrBytes,0,data,5,valueStrBytes.length);
        BeeFrame rxReciveData = new BeeFrame(FrameTypes.RECEIVED_DATA, data);
        bytes = new byte[]{
                (byte)0xFE, 0x14, 0x25, 0x00, 0x0E, 0x0E, 0x4F, 0x07, 0x49, 0x44, 0x3A, 0x33, 0x36, 0x33, 0x37, 0x20,
                0x36, 0x2E, 0x36, 0x32, 0x0D, 0x0A, 0x60
        };
        TestUtils.assertArrayEquals(rxReciveData.getBytes(), bytes);
    }


}