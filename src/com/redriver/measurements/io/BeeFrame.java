package com.redriver.measurements.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 全局命令帧格式：
 * 帧头( len = 1byte; val = 0xFE )
 * +长度( len = 1byte; val &lt; 98 )
 * +有效数据( len &lt; 98bytes; val = any )
 *      ++ 桢类型
 *      ++ 载荷数据
 * +校验( len = 1byes; val = 所有有效数据异或结果 )
 * <p/>
 * Created by zwq00000 on 2014/5/13.
 */
public final class BeeFrame {
    /**
     * 帧头标志位置
     */
    static final int POSITION_FRAME_HEAD = 0;
    /**
     * 数据长度位置
     */
    static final int POSITION_DATA_LENGTH = 1;
    /**
     * 帧类型位置
     */
    static final int POSITION_FRAME_TYPE = 2;

    /**
     * 载荷数据起始位置
     */
    static final int POSITION_DATA_PART_START = 3;
    /**
     * 消息部分起始地址
     */
    static final int POSITION_MESSAGE_PART = 5 + POSITION_DATA_PART_START;

    /**
     * 桢头标志
     */
    public static final byte FRAME_HEAD = (byte) 0xFE;

    /**
     * 接收桢 掩码
     */
    public static final byte RX_FRAME_MASK = (byte) 0x80;

    /**
     * 帧类型
     */
    private byte frameType;

    /**
     * 帧数据( len &lt; 98bytes; val = any )
     */
    final byte[] frameData;

    /**
     * 构造方法
     * @param bytes 包括帧类型的字节数组,第一个字节从帧类型开始
     */
    public BeeFrame(@NotNull byte[] bytes){
        this.frameData = new byte[bytes.length + 3];
        frameData[POSITION_FRAME_HEAD] = FRAME_HEAD;
        frameData[POSITION_DATA_LENGTH] = (byte)(bytes.length);
        System.arraycopy(bytes, 0, frameData, POSITION_FRAME_TYPE, bytes.length);
        byte dataLen = getDataLen();
        frameData[dataLen+2] = calculateChecksum(frameData,POSITION_FRAME_TYPE,dataLen);
    }

    /**
     * 构造方法
     *
     * @param frameType
     * @see FrameTypes#
     */
    //public byte Checksum;
    public BeeFrame(byte frameType) {
        this(new byte[]{frameType});
    }

    /**
     * @param frameType 帧类型
     * @param data      有效载荷数据
     */
    public BeeFrame(byte frameType, @NotNull byte[] data) {
        byte dataLen = (byte) (data.length + 0x1);
        this.frameData = new byte[dataLen + 3];
        frameData[POSITION_FRAME_HEAD] = FRAME_HEAD;
        frameData[POSITION_DATA_LENGTH] = dataLen;
        frameData[POSITION_FRAME_TYPE] = frameType;
        System.arraycopy(data, 0, frameData, POSITION_DATA_PART_START, data.length);
        frameData[dataLen+2] = calculateChecksum(frameData,POSITION_FRAME_TYPE,dataLen);
    }

    public BeeFrame(byte frameType, boolean isSuccess, int securityKey) {
        this(new byte[]{
                frameType,
                (byte) (isSuccess ? 1 : 0),
                (byte)((securityKey >> 24) & 0xFF),
                (byte)((securityKey >> 16) & 0xFF),
                (byte)((securityKey >> 8) & 0xFF),
                (byte)(securityKey & 0xFF)
        });
    }

    /**
     * 构造方法
     *
     * @param frameType   帧类型
     * @param securityKey 通讯秘钥
     */
    public BeeFrame(byte frameType, int securityKey) {
        this(new byte[]{
                frameType,
                (byte)((securityKey >> 24) & 0xFF),
                (byte)((securityKey >> 16) & 0xFF),
                (byte)((securityKey >> 8) & 0xFF),
                (byte)(securityKey & 0xFF)
        });
    }

    /**
     * 计算 Body 的校验值
     *
     * @param dataBody
     * @return dataBody 的校验值
     */
    private static byte calculateChecksum(@Nullable byte[] dataBody,int startPosition,int length) {
        if (dataBody == null || dataBody.length == 0) {
            return 0;
        }
        byte result = dataBody[startPosition];
        for (int i = startPosition+1; i < startPosition+length; i++) {
            result ^= dataBody[i];
        }
        return result;
    }

    /**
     * 有效数据( len &lt; 98bytes; val = any )
     *
     * @return 有效数据部分
     */
    public byte[] getData() {
        byte dataLen = getDataLen();
        byte[] data = new byte[dataLen];
        System.arraycopy(frameData,POSITION_FRAME_TYPE,data,0,dataLen);
        return data;
    }

    /**
     * 有效载荷数据长度( len = 1byte; val &lt; 98 )
     */
    public byte getDataLen() {
        return frameData[POSITION_DATA_LENGTH];
    }

    /**
     * 帧类型
     */
    public byte getFrameType() {
        return frameData[POSITION_FRAME_TYPE];
    }

    /**
     * 基本桢命令类型（移除 RX_FRAME 标志）
     *
     * @return
     */
    public byte getBaseFrameType() {
        return (byte) (getFrameType() & ~FrameTypes.RX_FRAME);
    }

    /**
     * 是否为接收桢
     *
     * @return
     */
    public boolean isRxFrame() {
        return (getFrameType() & FrameTypes.RX_FRAME) == FrameTypes.RX_FRAME;
    }

    /**
     * @return
     * @see com.redriver.measurements.io.BeeFrame#getData 字段的按位异或校验
     */
    public byte getCheckSum() {
        byte dataLen = getDataLen();
        return calculateChecksum(frameData,POSITION_FRAME_TYPE,dataLen);
    }

    /**
     * 写入到IO流
     *
     * @param outputStream
     * @throws java.io.IOException
     */
    public void writeTo(@NotNull DataOutputStream outputStream) throws IOException {
        outputStream.write(frameData, 0, frameData.length);
    }

    /**
     * 序列化 帧数据到 字节数组，包括帧头和校验位
     *
     * @return 返回写入串口的字节流数组
     */
    @NotNull
    public byte[] getBytes() {
        return frameData;
    }
}


