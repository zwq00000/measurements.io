package com.redriver.measurements.io;

import org.jetbrains.annotations.NotNull;

/**
 * 无线量具数据帧消息对象
 * 根据 @see FrameTypes 帧类型,对应类型化帧消息，便于后续处理
 * @see BeeFrameArgs 抽象类，提供基础容器
 * 增加类型化数据帧消息需要从此类型继承
 * Created by zwq00000 on 2014/5/15.
 */
public abstract class BeeFrameArgs {
    /// <summary>
    ///     帧数据
    /// </summary>
    protected final BeeFrame mFrame;

    private byte frameType;

    protected BeeFrameArgs(@NotNull BeeFrame frame){
        if(frame == null){
            throw new NullPointerException("mFrame is not been null");
        }
        this.mFrame = frame;
        frameType = frame.getBaseFrameType();
    }

    /**
     * 帧类型 @see FrameTypes
     * @return @see FrameTypes
     */
    public byte getFrameType(){
        return frameType;
    }

    /**
     * 事件说明
     * @return 事件内容说明
     */
    public abstract String getDescription();
}
