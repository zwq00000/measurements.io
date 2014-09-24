package android_serialport_api;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by zwq00000 on 2014/7/30.
 */
public interface SerialPortBase extends Closeable {


    /** 5 mFrame bits. */
    public static final int DATA_BITS_5 = 5;

    /** 6 mFrame bits. */
    public static final int DATA_BITS_6 = 6;

    /** 7 mFrame bits. */
    public static final int DATA_BITS_7 = 7;

    /** 8 mFrame bits. */
    public static final int DATA_BITS_8 = 8;

    /** No parity. */
    public static final int PARITY_NONE = 0;

    /** Odd parity. */
    public static final int PARITY_ODD = 1;

    /** Even parity. */
    public static final int PARITY_EVEN = 2;

    /** Mark parity. */
    public static final int PARITY_MARK = 3;

    /** Space parity. */
    public static final int PARITY_SPACE = 4;

    /** 1 stop bit. */
    public static final int STOP_BITS_1 = 1;

    /** 1.5 stop bits. */
    public static final int STOP_BITS_1_5 = 3;

    /** 2 stop bits. */
    public static final int STOP_BITS_2 = 2;


    /**
     * 获取输入流
     * @return
     */
    public InputStream getInputStream();

    /**
     * 获取 输出流
     * @return
     */
    public OutputStream getOutputStream();

    /**
     * Reads as many bytes as possible into the destination buffer.
     *
     * @param buffer the destination byte buffer
     * @return the actual number of bytes read
     * @throws java.io.IOException if an error occurred during reading
     */
    public int read(final byte[] buffer) throws IOException;

    /**
     * Equivalent to {@code write(buffer, 0, buffer.length)}.
     */
    public void write(byte[] buffer) throws IOException ;

    /**
     * Flushes this stream. Implementations of this method should ensure that
     * any buffered mFrame is written out. This implementation does nothing.
     *
     * @throws IOException
     *             if an error occurs while flushing this stream.
     */
    public void flush() throws IOException;
}
