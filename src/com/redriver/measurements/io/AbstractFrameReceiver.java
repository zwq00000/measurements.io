package com.redriver.measurements.io;

import com.redriver.measurements.core.MeasureRecord;

/**
 * Created by zwq00000 on 2014/7/22.
 */
public abstract class AbstractFrameReceiver implements FrameReceiver {

    /**
     * 接收到测量数据的事件侦听器
     */
    private DataReceivedListener mDataReceivedListener = null;

    protected AbstractFrameReceiver(){

    }

    protected AbstractFrameReceiver(DataReceivedListener dataListener){
        this.mDataReceivedListener = dataListener;
    }

    /**
     * 添加 数据接收侦听器
     * @param listener BeeFrameReceivedListener
     */
    @Override
    public synchronized void setDataReceivedListener(FrameReceiver.DataReceivedListener listener) {
        mDataReceivedListener = listener;
    }

    @Override
    public void OnReceivedData(MeasureRecord args) {
        if(mDataReceivedListener!=null){
            mDataReceivedListener.onDataReceived(args);
        }
    }

    /**
     * 是否存在 数据接收侦听器
     * @return
     */
    protected boolean hasDataReceivedListener(){
        return mDataReceivedListener != null;
    }
}
