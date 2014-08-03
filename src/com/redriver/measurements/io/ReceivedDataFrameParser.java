package com.redriver.measurements.io;

import android.annotation.TargetApi;
import android.os.Build;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.models.MeasureRecordImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据帧静态缓存
 * Created by zwq00000 on 2014/6/21.
 */
public final class ReceivedDataFrameParser {

    /**
     * 帧类型位置
     */
    static final int POSITION_FRAME_TYPE = 2;
    /**
     * 数据长度位置
     */
    static final int POSITION_DATA_LENGTH = 1;
    /**
     * 载荷数据起始位置
     */
    static final int POSITION_DATA_PART_START = 3;
    /**
     * 消息部分起始地址
     */
    private static final int POSITION_MESSAGE_PART = 5 + POSITION_DATA_PART_START;
    /**
     * 帧头标志位置
     */
    private static final int POSITION_FRAME_HEAD = 0;
    /**
     * 内容解析正则表达式
     */
    private static final Pattern MESSAGE_PARSE_REGEX = Pattern.compile("ID:(\\S{4})\\s+(-?\\d+(\\.\\d+)?)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS | Pattern.MULTILINE | Pattern.UNIX_LINES);
    /**
     * 字符串解析字符集
     */
    private static final Charset ASCII_CHARSET = Charset.forName("US-ASCII");
    /**
     * 数据帧最小长度
     */
    private static final int FRAME_MIN_SIZE = 10;
    /**
     * 缓冲使用长度
     */
    int bufferLength = 0;

    public static char[] getMessage() {
        //todo 实现获取消息方法
        return null;
    }

    /**
     * 计算校验值
     *
     * @return
     */
    private static byte getCheckSum(byte[] bytes, int offset, int dataLength) {
        if (dataLength == 0) {
            return 0;
        }
        byte result = bytes[offset];
        for (int i = offset + 1; i < offset + dataLength; i++) {
            result ^= bytes[i];
        }
        return (byte) result;
    }

    /**
     * 计算校验值
     *
     * @return
     */
    private static byte getCheckSum(byte[] bytes) {
        if (bytes.length == 0) {
            return 0;
        }
        int result = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            result ^= bytes[i];
        }
        return (byte) result;
    }

    /**
     * 接收数据
     *
     * @param reader
     * @return
     * @throws java.io.IOException
     */
    @NotNull
    public static MeasureRecord readFromStream(@NotNull InputStream reader) throws IOException {
        while (reader.available() > -1) {
            if (BeeFrame.FRAME_HEAD == (byte) reader.read()) {
                //读取到桢头
                byte dataLen = (byte) reader.read();
                if (dataLen > 1) {
                    //读取数据包
                    final byte[] frameData = new byte[dataLen];
                    int readLength = reader.read(frameData, 0, dataLen);
                    if(readLength<dataLen){
                        reader.read(frameData,readLength,dataLen-readLength);
                        //throw new IOException("read length "+readLength + " 预期读取 "+dataLen + " 字节");
                    }
                    int checksum = reader.read();
                    if (getCheckSum(frameData) != (byte) checksum) {
                        throw new IOException("数据包校验错误");
                    }
                    byte frameType = frameData[0];
                    if (FrameTypes.isReceivedDataFrame(frameType)) {
                        return createFromBytes(frameData, 0, dataLen);
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    public synchronized static MeasureRecord readFromBytes(@NotNull final byte[] bytes) throws IOException {
        if (bytes == null || bytes.length < FRAME_MIN_SIZE) {
            return null;
        }
        for (int i = 0; i < bytes.length - FRAME_MIN_SIZE; i++) {
            if (bytes[i] == BeeFrame.FRAME_HEAD) {
                return readFromBytes(bytes, i);
            }
        }
        return null;
    }

    @NotNull
    private synchronized static MeasureRecord readFromBytes(@NotNull final byte[] bytes, int offset) throws IOException {
        if (BeeFrame.FRAME_HEAD == bytes[POSITION_FRAME_HEAD + offset]) {
            //读取到桢头
            byte dataLen = bytes[offset + POSITION_DATA_LENGTH];
            if (dataLen > 1) {
                //读取数据包
                int checksum = bytes[offset + POSITION_FRAME_TYPE + dataLen];
                if (getCheckSum(bytes, offset + POSITION_FRAME_TYPE, dataLen) != (byte) checksum) {
                    throw new IOException("数据包校验错误");
                }
                if (bytes[offset + POSITION_FRAME_TYPE] == FrameTypes.RX_RECEIVED_DATA_FRAME) {
                    return createFromBytes(bytes, offset + POSITION_FRAME_TYPE, dataLen);
                }
            }
        }
        return null;
    }

    /**
     * 构造 测量记录
     *
     * @param buffer         接收数据缓冲
     * @param dataPartOffset 有效数据载荷起始位置,包括帧类型位置
     * @return {@link com.redriver.measurements.core.MeasureRecord}
     */
    @NotNull
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static MeasureRecord createFromBytes(byte[] buffer, int dataPartOffset, int dataLen) {
        byte pushType = buffer[dataPartOffset+1];
        //record.packageId = buffer[2 + POSITION_DATA_PART_START];
        short batteryValue = ByteUtils.bytesToShort(buffer, 3 + dataPartOffset);
        String message = new String(buffer, 5 + dataPartOffset, dataLen - 5, ASCII_CHARSET);
        Matcher matcher = MESSAGE_PARSE_REGEX.matcher(message);
        if (matcher.find()) {
            String gageId = matcher.group(1);
            String rawValue = matcher.group(2);
            return MeasureRecordImpl.obtain(pushType, batteryValue, new Date(), gageId, rawValue);
        } else {
            throw new FrameTypeException(String.format("消息格式不正确,%s", message));
        }
    }

    /**
     * 从{@link java.nio.ByteBuffer 读取数据}
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public static MeasureRecord readFromBytes(@NotNull final ByteBuffer buffer) throws IOException {
        int limit = buffer.limit();
        int position = buffer.position();
        for (int i = position; i < limit - FRAME_MIN_SIZE; i++) {
            if (buffer.get() == BeeFrame.FRAME_HEAD) {
                //读取到桢头
                byte dataLen = buffer.get();
                if (dataLen > 1) {
                    byte[] bytes = new byte[dataLen];
                    buffer.get(bytes, 0, dataLen);
                    //读取数据包
                    int checksum = buffer.get();
                    if (getCheckSum(bytes) != (byte) checksum) {
                        throw new IOException("数据包校验错误");
                    }
                    if (FrameTypes.isReceivedDataFrame(bytes[0])) {
                        return createFromBytes(bytes, 0, dataLen);
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
