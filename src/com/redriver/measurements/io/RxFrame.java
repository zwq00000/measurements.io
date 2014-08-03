package com.redriver.measurements.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zwq00000 on 2014/5/21.
 */
public final class RxFrame {

    /**
     * 从数据流读取数据帧
     * @param inputStream
     * @return
     * @throws java.io.IOException
     */
    @NotNull
    public static final BeeFrame ReadFrame(@NotNull InputStream inputStream) throws IOException {
        while (true){
            if (inputStream.read() == BeeFrame.FRAME_HEAD){
                //读取到桢头
                byte dataLen = (byte) inputStream.read();
                //读取数据包
                byte[] data = new byte[dataLen];
                inputStream.read(data, 0, data.length);
                BeeFrame frame = new BeeFrame(data);
                int checksum = inputStream.read();
                if (frame.getCheckSum() != checksum){
                    throw new IOException(String.format("数据包校验错误,0x%d - 0x%d", frame.getCheckSum(), checksum));
                }
                return frame;
            }
        }
    }
}
