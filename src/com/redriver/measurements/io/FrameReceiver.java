package com.redriver.measurements.io;

import android.content.Context;
import android_serialport_api.SerialPort;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.serial.SerialPortReceiver;
import com.redriver.measurements.io.usb.UsbFrameReceiver;

import java.io.IOException;

/**
 * 测量数据帧接收器
 * Created by zwq00000 on 2014/7/22.
 */
public abstract class FrameReceiver implements IFrameReceiver {

    /**
     * 接收到测量数据的事件侦听器
     */
    private DataReceivedListener mDataReceivedListener = null;

    private boolean isClosed;

    protected FrameReceiver(){
        isClosed = true;
    }

    protected FrameReceiver(DataReceivedListener dataListener){
        this();
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

    @Override
    public void open(){
        if(!isClosed){
            return;
        }
        try {
            openInternal();
            isClosed=false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(){
        try {
            closeInternal();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            isClosed = true;
        }
    }

    public boolean isClosed(){
        return isClosed;
    }

    /**
     * 打开接收器 内部实现的
     * @throws IOException
     */
    protected abstract void openInternal() throws IOException;

    /**
     * 关闭接收器 内部实现
     * @throws IOException
     */
    protected abstract void closeInternal() throws IOException;

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
}
