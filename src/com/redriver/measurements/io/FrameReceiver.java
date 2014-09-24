package com.redriver.measurements.io;

import android.content.Context;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.SerialPort.SerialPortReceiver;
import com.redriver.measurements.io.usb.UsbFrameReceiver;

/**
 * 测量数据帧接收器
 * Created by zwq00000 on 2014/7/22.
 */
public abstract class FrameReceiver implements IFrameReceiver {

    /**
     * 接收到测量数据的事件侦听器
     */
    private DataReceivedListener mDataReceivedListener = null;

    protected FrameReceiver(){
    }

    protected FrameReceiver(DataReceivedListener dataListener){
        this.mDataReceivedListener = dataListener;
    }

    /**
     * 添加 数据接收侦听器
     * @param listener BeeFrameReceivedListener
     */
    @Override
    public synchronized void setDataReceivedListener(IFrameReceiver.DataReceivedListener listener) {
        mDataReceivedListener = listener;
    }

    @Override
    public void OnReceivedData(MeasureRecord args) {
        if(mDataReceivedListener!=null){
            mDataReceivedListener.onDataReceived(args);
        }
    }

    /**
     * 断开连接，销毁占用的资源
     */
    public void Terminate(){

    }

    /**
     * 是否存在 数据接收侦听器
     * @return
     */
    protected boolean hasDataReceivedListener(){
        return mDataReceivedListener != null;
    }

    public  static FrameReceiver CreateReceiver(Context context){
        String receiverType = SerialPortPreferences.getReceiverType(context);
        if(receiverType.equalsIgnoreCase(UsbFrameReceiver.class.getName())){
            return  new UsbFrameReceiver(context);
        }
        if(receiverType.equalsIgnoreCase(SerialPortReceiver.class.getName())){
            return new SerialPortReceiver(context);
        }
        //使用默认 接收器类型
        return  new UsbFrameReceiver(context);
    }
}
