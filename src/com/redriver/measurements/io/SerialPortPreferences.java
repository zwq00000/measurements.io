package com.redriver.measurements.io;

import android.content.Context;
import android.content.SharedPreferences;
import com.hoho.android.usbserial.SerialPortParameters;
import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * 应用程序首选项
 * Created by zwq00000 on 2014/7/20.
 */
/*package only*/

 public final class SerialPortPreferences {

    /**
     * 波特率 键名
     */
    private static final String KEY_BAUD_RATE = "baudRate";
    /**
     * 数据位
     */
    private static final String KEY_DATA_BITS = "dataBits";
    /**
     * 停止位 键名
     */
    private static final String KEY_STOP_BITS = "stopBits";

    /**
     * 校验类型
     */
    private static final String KEY_PARITY = "parity";

    /**
     * 默认端口名称
     */
    private static final String KEY_DEFAULT_PORT_NAME = "portName";

    /**
     * 获取端口名称
     * @param context
     * @return
     */
    public static String getPortName(Context context){
        return getValue(context,KEY_DEFAULT_PORT_NAME,"");
    }

    /**
     * 设置默认端口名称
     * @param context
     * @param portName
     */
    public static void setPortName(Context context,String portName){
        setValue(context,KEY_DEFAULT_PORT_NAME,portName);
    }

    /**
     * 获取外部存储路径
     *
     * @param context
     * @return
     */
    public static int getBaudRate(Context context) {
        return getIntValue(context, KEY_BAUD_RATE, 115200);
    }

    /**
     * 设置 波特率
     *
     * @param context
     * @param baudRate
     */
    public static void setBaudRate(Context context, int baudRate) {
        setIntValue(context, KEY_BAUD_RATE, baudRate);
    }

    public static int getDataBits(Context context) {
        return getIntValue(context, KEY_DATA_BITS, UsbSerialPort.DATABITS_8);
    }

    public static void setDataBits(Context context, int dataBits) {
        setIntValue(context, KEY_DATA_BITS, dataBits);
    }

    public static int getStopBits(Context context) {
        return getIntValue(context, KEY_STOP_BITS, UsbSerialPort.STOPBITS_1);
    }

    public static int getParity(Context context) {
        return getIntValue(context, KEY_PARITY, UsbSerialPort.PARITY_NONE);
    }

    public static void setParity(Context context, int parity) {
        setIntValue(context, KEY_PARITY, parity);
    }

    public static SerialPortParameters getParameters(Context context) {
        int baudRate = getBaudRate(context);
        int dataBits = getDataBits(context);
        int stopBits = getStopBits(context);
        int parity = getParity(context);
        return new SerialPortParameters(baudRate, dataBits, stopBits, parity);
    }

    public static void setParameters(Context context, SerialPortParameters parameters) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(KEY_BAUD_RATE, parameters.baudRate);
        editor.putInt(KEY_DATA_BITS, parameters.dataBits);
        editor.putInt(KEY_STOP_BITS, parameters.stopBits);
        editor.putInt(KEY_PARITY, parameters.parity);
        editor.commit();
    }

    public static void setStopBits(Context context, int stopBits) {
        setIntValue(context, KEY_STOP_BITS, stopBits);
    }

    private static String getValue(Context context, String key, String defaultValue) {
        String preferencesName = context.getPackageName();
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    private static int getIntValue(Context context, String key, int defaultValue) {
        String preferencesName = context.getPackageName();
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return preferences.getInt(key, defaultValue);
    }

    private static void setValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value);
        editor.commit();
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        String preferencesName = context.getPackageName();
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return preferences.edit();
    }

    private static void setIntValue(Context context, String key, int value) {
        String preferencesName = context.getPackageName();
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
