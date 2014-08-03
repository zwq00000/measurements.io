package com.redriver.measurements.io.test;

import android.annotation.TargetApi;
import android.os.Build;
import com.redriver.measurements.io.BeeFrame;
import com.redriver.measurements.io.FrameTypes;
import junit.framework.TestCase;

import java.nio.charset.Charset;

/**
 * Created by zwq00000 on 2014/5/15.
 */
public class TestUtils extends TestCase {
    public static final byte[] FrameBytes = new byte[]{
            (byte)0xFE,(byte)0x16,(byte)0xA5,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x49,(byte)0x44,(byte)0x3A,(byte)0x30,(byte)0x30,(byte)0x30,(byte)0x33,(byte)0x20,(byte)0x31,(byte)0x32,(byte)0x2E,(byte)0x31,(byte)0x32,(byte)0x33,(byte)0x0D,(byte)0x0A,(byte)0xAB
    };

    public static final byte[] getFrameData(){
        byte[] data = new byte[FrameBytes.length - 3];
        System.arraycopy(FrameBytes,3,data,0,data.length);
        return data;
    }

    private static byte _packageId = 0;
    /**
     *  生成 接收数据帧
     * @param testStr 格式为 "ID:0001 12.123\r\n"
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static final BeeFrame genReceiveBeeFrame(String testStr)   {
        byte[] valueArary = testStr.getBytes(Charset.forName("US-ASCII"));
        byte[]  data = new byte[valueArary.length + 5];
        data[0] = 0;
        data[1] = 0;
        System.arraycopy(valueArary,0,data,5,valueArary.length);
        BeeFrame frame = new BeeFrame((byte)(FrameTypes.RECEIVED_DATA | FrameTypes.RX_FRAME),data);
        return frame;
    }

    /**
     * 生成 接收数据帧
     * @param gageId 量具Id
     * @param value 测量值
     * @return
     */
    public static final BeeFrame genReciveBeeFrame(String gageId,double value)  {
        return genReceiveBeeFrame(String.format("ID:%s %d", gageId, value));
    }

    /**
     * 生成 接收数据帧
     * @param gageId 量具Id
     * @param rawValue 测量值读数
     * @return
     */
    public static final BeeFrame genReceiveBeeFrame(String gageId, String rawValue)  {
        return genReceiveBeeFrame(String.format("ID:%s %s\r\n", gageId, rawValue));
    }

    public static void assertArrayEquals(byte[] expected, byte[] actual) {
        if(expected == null){
            assertNull(actual);
        }
        assertEquals(expected.length, actual.length);
        for(int i=0;i<expected.length;i++){
            assertEquals(expected[i],actual[i]);
        }
    }
}
