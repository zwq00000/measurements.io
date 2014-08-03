package com.redriver.measurements.io;

/**
 * 帧类型错误异常
 * Created by zwq00000 on 2014/5/15.
 */
public class FrameTypeException  extends RuntimeException{

    public FrameTypeException(String detailMessage ){
        super(detailMessage);
    }
}
