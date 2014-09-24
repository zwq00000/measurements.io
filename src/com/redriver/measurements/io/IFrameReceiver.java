package com.redriver.measurements.io;

import com.redriver.measurements.core.MeasureRecord;

import java.io.Closeable;
import java.io.IOException;
import java.util.EventListener;

/**
 * Created by zwq00000 on 2014/7/22.
 */
public interface IFrameReceiver extends Closeable {
    /**
     * 设置 侦听器
     * @param listener
     */
    void setDataReceivedListener(DataReceivedListener listener);

    /**
     * 响应接收到测量数据事件
     * @param args
     */
    void OnReceivedData(MeasureRecord args);


    /**
     * 打开连接
     * @return  打开状态
     */
    public void open() throws IOException;


    /**
     * 接收到测量数据数据帧接口
     */
    public interface DataReceivedListener extends EventListener {
        /**
         * 接收到测量数据数据帧 帧类型为 @see
         * @param event
         */
        public void onDataReceived(MeasureRecord event);
    }

    public interface OnFrameReceivedListener extends EventListener{

        /**
         * 接收到 @see BeeFrame (包括全部帧类型)
         * @param args
         */
        public void onFrameReceived(BeeFrameArgs args);
    }
}
