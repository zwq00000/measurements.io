package com.redriver.measurements.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import com.hoho.android.usbserial.SerialPortParameters;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.redriver.measurements.io.serial.SerialPortReceiver;
import com.redriver.measurements.io.usb.UsbFrameReceiver;

import java.io.IOException;

/**
 * 测量帧 接收器 应用程序首选项
 * Created by zwq00000 on 2014/7/20.
 */
public final class FrameReceiverPreferences {

    /**
     * 奇偶校验 默认值
     */
    private static int DEFAULT_PARITY;
    /**
     * 数据位 默认值
     */
    private static int DEFAULT_DATA_BITS;
    /**
     * 波特率 键名
     */
    static String KEY_BAUD_RATE;
    /**
     * 数据位
     */
    static String KEY_DATA_BITS;
    /**
     * 停止位 键名
     */
    static String KEY_STOP_BITS;

    /**
     * 校验类型
     */
    static String KEY_PARITY;

    /**
     * 默认端口名称
     */
    static String KEY_PORT_NAME = null;

    /**
     * 接收器类型 存储键
     */
    static String KEY_USE_SERIAL_RECEIVER;
    /**
     * 默认 波特率 115200
     */
    private static int DEFAULT_BAUD_RATE;
    /**
     * 默认实例
     */
    private static FrameReceiverPreferences mInstance;
    /**
     *
     * 应用首选项
     */
    private final SharedPreferences preferences;
    /**
     * 上下文
     */
    private final Context mContext;
    /**
     * 接收器实例
     */
    private FrameReceiver mReceiver;

    private FrameReceiverPreferences(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
        if(KEY_PORT_NAME == null) {
            Resources resource = context.getResources();
            KEY_PORT_NAME = resource.getString(R.string.key_port_name);
            KEY_BAUD_RATE = resource.getString(R.string.key_baud_rate);
            KEY_DATA_BITS = resource.getString(R.string.key_data_bits);
            KEY_PARITY = resource.getString(R.string.key_parity);
            KEY_STOP_BITS = resource.getString(R.string.key_stop_bits);
            KEY_USE_SERIAL_RECEIVER = resource.getString(R.string.key_use_serial_receiver);
            DEFAULT_BAUD_RATE = resource.getInteger(R.integer.default_baud_rate);
            DEFAULT_DATA_BITS = resource.getInteger(R.integer.default_data_bits);
            DEFAULT_PARITY = resource.getInteger(R.integer.default_parity);
        }
    }

    /**
     * 获取 串口配置实例
     * @param context
     * @return
     */
    public static FrameReceiverPreferences getInstance(Context context) {
        if(mInstance ==null) {
            mInstance = new FrameReceiverPreferences(context);
        }
        return mInstance;
    }

    /**
     * 获取端口名称
     *
     * @return
     */
    public String getPortName() {
        return preferences.getString(KEY_PORT_NAME, "");
    }

    /**
     * 设置默认端口名称
     *
     * @param portName
     */
    public void setPortName(String portName) {
        setValue(KEY_PORT_NAME, portName);
    }

    private void setValue(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    /**
     * 获取 串口波特率
     *
     * @return
     */
    public int getBaudRate() {
        return preferences.getInt(KEY_BAUD_RATE, DEFAULT_BAUD_RATE);
    }

    /**
     * 设置 波特率
     *
     * @param baudRate
     */
    public void setBaudRate(int baudRate) {
        setValue(KEY_BAUD_RATE, baudRate);
    }

    private void setValue(String key, int value) {
        preferences.edit().putInt(key, value).commit();
    }

    private void setValue(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 获取 数据位 设置
     *
     * @return
     */
    public int getDataBits() {
        return preferences.getInt(KEY_DATA_BITS,DEFAULT_DATA_BITS);
    }

    /**
     * 设置 数据位
     *
     * @param dataBits
     */
    public void setDataBits(int dataBits) {
        switch (dataBits) {
            case UsbSerialPort.DATABITS_5:
            case UsbSerialPort.DATABITS_6:
            case UsbSerialPort.DATABITS_7:
            case UsbSerialPort.DATABITS_8:
                setValue(KEY_DATA_BITS, dataBits);
                break;
            default:
                setValue(KEY_DATA_BITS, UsbSerialPort.DATABITS_8);
        }
    }

    /**
     * 获取 停止位 默认为 {@link com.hoho.android.usbserial.driver.UsbSerialPort#STOPBITS_1}
     *
     * @return
     */
    public int getStopBits() {
        return preferences.getInt(KEY_STOP_BITS, UsbSerialPort.STOPBITS_1);
    }

    /**
     * 获取 奇偶校验
     * 默认值 {@value UsbSerialPort#PARITY_NONE}
     *
     * @return {@value UsbSerialPort#PARITY_EVEN}、{@value UsbSerialPort#PARITY_MARK}、
     * {@value UsbSerialPort#PARITY_NONE}、{@value UsbSerialPort#PARITY_ODD}、{@value UsbSerialPort#PARITY_SPACE}
     */
    public int getParity() {
        return preferences.getInt(KEY_PARITY, UsbSerialPort.PARITY_NONE);
    }

    /**
     * 奇偶校验
     *
     * @param parity
     */
    public void setParity(int parity) {
        switch (parity) {
            case UsbSerialPort.PARITY_SPACE:
            case UsbSerialPort.PARITY_ODD:
            case UsbSerialPort.PARITY_MARK:
            case UsbSerialPort.PARITY_NONE:
            case UsbSerialPort.PARITY_EVEN:
                setValue(KEY_PARITY, parity);
                break;
            default:
                setValue(KEY_PARITY, UsbSerialPort.PARITY_NONE);
        }
    }

    /**
     * 获取 串口参数
     * @return
     */
    public SerialPortParameters getParameters() {
        int baudRate = getBaudRate();
        int dataBits = getDataBits();
        int stopBits = getStopBits();
        int parity = getParity();
        return new SerialPortParameters(baudRate, dataBits, stopBits, parity);
    }

    /**
     * 设置串口参数
     * @param parameters
     */
    public void setParameters(SerialPortParameters parameters) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_BAUD_RATE, parameters.baudRate);
        editor.putInt(KEY_DATA_BITS, parameters.dataBits);
        editor.putInt(KEY_STOP_BITS, parameters.stopBits);
        editor.putInt(KEY_PARITY, parameters.parity);
        editor.commit();
    }

    /**
     * 获取 接收器类型
     *
     * @return
     */
    public String getReceiverType() {
        //是否使用串口接收器 true 为串口,false 为USB口
        boolean useSerialReceiver = preferences.getBoolean(KEY_USE_SERIAL_RECEIVER, true);
        if(useSerialReceiver){
            return SerialPortReceiver.class.getName();
        }else{
            return UsbFrameReceiver.class.getName();
        }
    }

    /**
     * 根据配置参数 获取接收器实例
     * @return
     */
    public FrameReceiver getFrameReceiver(){
        //是否使用串口接收器 true 为串口,false 为USB口
        boolean useSerialReceiver = preferences.getBoolean(KEY_USE_SERIAL_RECEIVER, true);

        if(mReceiver!=null){
            //配置改变，销毁原有接收器实例
            if(useSerialReceiver != mReceiver instanceof SerialPortReceiver){
                try {
                    mReceiver.Terminate();
                }finally {
                    mReceiver = null;
                }
            }
        }

        if(this.mReceiver == null || this.mReceiver.isClosed()){
            if(useSerialReceiver){
                mReceiver = new SerialPortReceiver(mContext);
            }else{
                mReceiver = new UsbFrameReceiver(mContext);
            }
        }
        return mReceiver;
    }

    /**
     * 设置 使用 USB 接收器
     */
    public void setUseUsbReceiver(boolean useUsbReceiver) {
            setValue(KEY_USE_SERIAL_RECEIVER, !useUsbReceiver);
    }
}

