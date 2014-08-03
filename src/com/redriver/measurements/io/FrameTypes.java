package com.redriver.measurements.io;

import org.jetbrains.annotations.NotNull;

/**
 * 数据帧类型标志
 * @author zwq00000
 *
 */
public final class FrameTypes {

    /**
     * 接收桢标志
     */
    public static final byte RX_FRAME = (byte) 0x80;

    /**
     * 回测命令: 0x3F
     */
    public static final byte ECHO = (byte) 0x3F;

    /**
     * 取接收器信息命令:0x31
     */
    public static final byte READ_RECEIVER_INFO = (byte) 0x31;

    /**
     * 恢复出厂设置:0x38
     */
    public static final byte FACTORY_RESET = (byte) 0x38;

    /**
     * 配置中心通信密钥:0x21
     */
    public static final byte SET_RECEIVER_KEY = (byte) 0x21;

    /**
     * 读取中心通信密钥: 0x22
     */
    public static final byte READ_RECEIVER_KEY = (byte) 0x22;

    /**
     * 配置其它设备通信密钥:0x23
     */
    public static final byte SET_DEVICE_KEY = (byte) 0x23;

    /**
     * 读取其密设备通信密钥:0x24
     */
    public static final byte READ_DEVICE_KEY = (byte) 0x24;

    /**
     * 接收 远端数据 0x25
     */
    public static final byte RECEIVED_DATA = (byte) 0x25;

    /**
     * 接收数据桢标志
     */
    public static final byte RX_RECEIVED_DATA_FRAME = (byte) (FrameTypes.RX_FRAME | FrameTypes.RECEIVED_DATA);

    /**
     * 基本桢命令类型（移除 RX_FRAME 标志）
     *
     * @return
     */
    public static byte getBaseFrameType(byte frameType) {
        return (byte) (frameType & ~FrameTypes.RX_FRAME);
    }

    /**
     * 是否为接收桢
     *
     * @return
     */
    public static boolean isRxFrame(byte frameType) {
        return (frameType & FrameTypes.RX_FRAME) == FrameTypes.RX_FRAME;
    }

    @NotNull
    public static String toDescription(byte frameType){
        frameType = getBaseFrameType(frameType);
        switch (frameType){
            case ECHO:
                return "回测命令";
            case READ_RECEIVER_INFO:
                return "取接收器信息命令";
            case FACTORY_RESET:
                return "恢复出厂设置";
            case SET_RECEIVER_KEY:
                return "配置中心通信密钥";
            case READ_RECEIVER_KEY:
                return "读取中心通信密钥";
            case SET_DEVICE_KEY:
                return "配置其它设备通信密钥";
            case READ_DEVICE_KEY:
                return "读取其密设备通信密钥";
            case RECEIVED_DATA:
                return "接收数据帧";
            default:
                return "未知帧类型 "+frameType;
        }
    }

    /**
     * 是否为 数据接收帧 （忽略 RxFrame 部分）
     * {@link #RECEIVED_DATA}
     * @param frameType
     * @return
     */
    public static boolean isReceivedDataFrame(byte frameType) {
        return (frameType & RECEIVED_DATA) == RECEIVED_DATA;
    }
}


