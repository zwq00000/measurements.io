package com.redriver.measurements.usb;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

/**
 * Created by zwq00000 on 2014/7/24.
 */
public class ByteBufferTest extends TestCase {

    public void testPut() throws Exception{

        //new FileInputStream().getChannel().read()

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        showBufferStatus(buffer);
        buffer.putInt(1234);
        System.out.println("put byte...");
        showBufferStatus(buffer);

        buffer.put(new byte[100]);
        System.out.println("put bytes 100...");
        showBufferStatus(buffer);

        int getValue = buffer.getInt();
        System.out.println("get int " + getValue);
        showBufferStatus(buffer);

        buffer.flip();
        System.out.println("flip...");
        getValue = buffer.getInt();
        System.out.println("get int " + getValue);
        showBufferStatus(buffer);

        /*buffer.limit(120);
        System.out.println("set limit 120...");
        showBufferStatus(buffer);*/

        buffer.flip();
        System.out.println("flip...");
        showBufferStatus(buffer);

        buffer.rewind();
        System.out.println("rewind...");
        showBufferStatus(buffer);

        buffer.remaining();
        System.out.println("remaining...");
        showBufferStatus(buffer);

    }

    public void testPosition() throws Exception{

        //new FileInputStream().getChannel().read()

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        showBufferStatus(buffer);
        for (int i=0;i<1024/4;i++) {
           buffer.putInt(i);
        }
        showBufferStatus(buffer);

        buffer.position(100);
        int getInt = buffer.getInt();
        System.out.println("getInt:" + getInt);
    }

    private void showBufferStatus(ByteBuffer buffer){
        System.out.println("position:"+buffer.position() + "\tremaining:" + buffer.remaining() + "\tlimit:" + buffer.limit() + "\tcapacity:"+buffer.capacity());
    }
}
