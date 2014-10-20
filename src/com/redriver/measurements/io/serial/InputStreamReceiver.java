package com.redriver.measurements.io.serial;

import android.util.Log;
import com.redriver.measurements.core.MeasureRecord;
import com.redriver.measurements.io.FrameReceiver;
import com.redriver.measurements.io.ReceivedDataFrameParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * 基于 {@link java.io.InputStream} 基本IO 流的 接收器
 * @see com.redriver.measurements.io.BeeFrame 数据帧接收器
 * Created by zwq00000 on 2014/5/18.
 */
@Deprecated
public final class InputStreamReceiver extends FrameReceiver implements  Runnable {
    private static final String TAG = "BeeFrameReceiver";

    private final InputStream mReceiverStream;
    private boolean isClosed;
    public InputStreamReceiver(InputStream receiverStream){
        if(receiverStream == null){
            throw new NullPointerException("receiver Stream is not been null");
        }
        this.mReceiverStream = receiverStream;
        isClosed = false;
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
        if(mReceiverStream == null){
            throw new IllegalArgumentException("Receiver SerialPort InputStream is Not Null");
        }
        while (true){
            if(isClosed){
                return;
            }
            try {
                MeasureRecord record = ReceivedDataFrameParser.readFromStream(mReceiverStream);
                if(record!=null) {
                    if(hasDataReceivedListener()){
                        OnReceivedData(record);
                    }
                }
            } catch (IOException e) {
                Log.d(TAG,e.getMessage());
            }catch (Exception e){
                //拦截未知异常
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * 打开连接
     *
     * @return 打开状态
     */
    @Override
    public void open() throws IOException {
    }

    /**
     * Closes the object and release any system resources it holds.
     * <p/>
     * <p>Although only the first call has any effect, it is safe to call close
     * multiple times on the same object. This is more lenient than the
     * overridden {@code AutoCloseable.close()}, which may be called at most
     * once.
     */
    @Override
    public void close() throws IOException {
        mReceiverStream.close();
    }
}
