package com.hoho.android.usbserial.util;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
* Created by zwq00000 on 2014/7/26.
*/
public interface Listener {
    /**
     * Called when new incoming data is available.
     */
    public void onNewData(ByteBuffer data) throws IOException;

    /**
     * Called when {@link com.hoho.android.usbserial.util.SerialInputOutputManager#run()} aborts due to an
     * error.
     */
    public void onRunError(Exception e);
}
