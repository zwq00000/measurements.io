package com.redriver.measurements.io.usb;

import android.view.KeyCharacterMap;
import android.view.KeyEvent;

/**
 * 条码读取器
 * 注册窗体中 继承 Activity的dispatchKeyEvent 方法
 * 在 {@link android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)} 中拦截KeyEvent
 *
 * {@code
 *  @Override
 *  public boolean dispatchKeyEvent(KeyEvent event){
 *      barCodeReader.onDispatchKeyEvent(event);
 *      return super.dispatchKeyEvent(event);
 *  }
 *
 *  由于供电问题，平板中可能无法连接 二维码条码器
 * Created by zwq00000 on 2014/7/28.
 */
public class BarCodeReader {
    private static final String TAG = "BarCodeReader";
    /**
     * 条码最小长度
     */
    private static final int MIN_BARCODE_LENGTH = 5;
    /**
     * 条码之间延迟时间 （毫秒）
     */
    private static final int DELAY_MILLIS = 50;
    /**
     * 条码结果侦听器
     */
    private final BarCodeReadListener mListener;
    private final KeyCharacterMap mFullKeyCharacterMap;
    /**
     * 读取字符缓存
     */
    private final char[] mReadBuffer;
    private long mLastReadTime = 0;
    private int mBufferLength;

    public BarCodeReader(BarCodeReadListener listener) {
        mReadBuffer = new char[256];
        this.mListener = listener;
        mFullKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.FULL);
    }

    /**
     * 注册窗体中 继承 Activity的dispatchKeyEvent 方法
     * 在 {@link android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)} 中拦截KeyEvent
     *
     * {@code
     *  @Override
     *  public boolean dispatchKeyEvent(KeyEvent event){
     *      barCodeReader.onDispatchKeyEvent(event);
     *      return super.dispatchKeyEvent(event);
     *  }
     * @param event
     */
    public void onDispatchKeyEvent(KeyEvent event){
        int action = event.getAction();
        if(action == KeyEvent.ACTION_DOWN){
            long currentTime = System.currentTimeMillis();
            long delayTime = currentTime - mLastReadTime;
            //根据延迟时间 屏蔽键盘手工输入
            if (delayTime > DELAY_MILLIS*2) {
                mBufferLength = 0;
            }
            int keyCode = event.getKeyCode();
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    onBarCodeReadCompleted();
                    break;
                default:
                    int c = mFullKeyCharacterMap.get(keyCode, event.getMetaState());
                    mReadBuffer[mBufferLength] = (char) c;
                    mBufferLength++;
                    mLastReadTime = currentTime;
            }
        }
    }

    protected void onBarCodeReadCompleted() {
        try {
            synchronized (mReadBuffer) {
                if (mBufferLength > MIN_BARCODE_LENGTH && mListener != null) {
                    mListener.onReadCompleted(new String(mReadBuffer, 0, mBufferLength - 1));
                }
            }
        } catch (Exception e) {
        } finally {
            mBufferLength = 0;
            mLastReadTime = 0;
        }
    }

    public interface BarCodeReadListener {
        /**
         * 条码读取完成
         *
         * @param barcode
         */
        void onReadCompleted(String barcode);
    }
}
