package com.redriver.measurements.io.test;

import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.BeeFrame;
import com.redriver.measurements.io.ReceivedDataFrameParser;
import junit.framework.TestCase;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class ReceivedDataFrameParserTest extends TestCase {
    private Runtime s_runtime;

    public void testReadFromBytes() throws Exception {
        String gageId = "0003";
        String receiveMsg = "ID:0003 12.123\r\n";
        byte[] data = receiveMsg.getBytes() ;
        BeeFrame frame = TestUtils.genReceiveBeeFrame(receiveMsg);

        byte[]  bytes = frame.getBytes();

        MeasureRecord record = ReceivedDataFrameParser.readFromBytes(bytes);
        assertNotNull(record);
        assertEquals(record.getGageId(),gageId);

        byte[] doubleBytes = new byte[bytes.length*2];
        System.arraycopy(bytes,0,doubleBytes,bytes.length-1,bytes.length);
        record = ReceivedDataFrameParser.readFromBytes(doubleBytes);
        assertNotNull(record);
        assertEquals(record.getGageId(),gageId);
    }

    public void testByteBuffer() throws Exception{
        String gageId = "0003";
        String receiveMsg = "ID:0003 12.123\r\n";
        byte[] data = receiveMsg.getBytes() ;
        BeeFrame frame = TestUtils.genReceiveBeeFrame(receiveMsg);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for(int i=0;i<100;i++) {
            System.out.println("step "+ i);
            buffer.clear();
            byte[] tmpBytes = new byte[i];
            buffer.put(tmpBytes);
            buffer.put(frame.getBytes());
            buffer.flip();
            MeasureRecord record = ReceivedDataFrameParser.readFromBytes(buffer);
            assertNotNull(record);
            assertEquals(record.getGageId(), gageId);
        }
    }

    private static MeasureRecord createFromBuffer(ByteBuffer buffer){
        buffer.flip();
        byte header = buffer.get();
        assertEquals(header,BeeFrame.FRAME_HEAD);
        byte len = buffer.get();
        byte cmd = buffer.get();
        byte pushType = buffer.get();
        buffer.get();
        buffer.get();
        short betValue = buffer.getShort();
        //byte[] charBuffer = new byte[len - 6];
        //buffer.get(charBuffer);

        CharBuffer  charBuffer = Charset.forName("US-ASCII").decode(buffer);
        System.out.println(charBuffer);
        char[] gageIdBuf = new char[4];
        CharSequence gageId = charBuffer.subSequence(3, 7);
        System.out.println(gageId);
        assertEquals(gageId.toString(),"0001");
        /*char[] chars = new char[len-6];
        while (charBuffer.hasRemaining()){
            System.out.print(charBuffer.get());
        }*/
        /*charBuffer.get(chars);
        System.out.println(new String(chars));*/
        return null;
    }

    public void testReadFromStream() throws Exception {
        final PipedInputStream inputStream = new PipedInputStream();
        final OutputStream outputStream = new PipedOutputStream(inputStream);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NumberFormat format = new DecimalFormat("###.###");
                Random random = new Random();
                while (true) {
                    BeeFrame frame = TestUtils.genReceiveBeeFrame("0001", format.format(random.nextDouble() * 10));
                    try {
                        outputStream.write(frame.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        s_runtime = Runtime.getRuntime();
        for (int i=0;i<100;i++) {
            try {
                MeasureRecord record = ReceivedDataFrameParser.readFromStream(inputStream);
                assertEquals(record.getGageId(), "0001");
                System.out.println(record.toString());
                record.recycle();
                //Thread.sleep(100);
                showMemoryUsage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void  showMemoryUsage(){
        System.out.println("free:" + s_runtime.freeMemory()/1024 + "\ttotal:" + s_runtime.totalMemory()/1024 + "\tused:"+ getUsedMemory()/1024);
    }

    /**
     *
     * 堆中已使用内存
     *
     * @return 堆中已使用内存
     */
    private long getUsedMemory() {
        return s_runtime.totalMemory() - s_runtime.freeMemory();
    }
}