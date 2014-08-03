package com.redriver.measurements.io;

import android.annotation.TargetApi;
import android.os.Build;
import com.redriver.measurements.core.MeasureRecord;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zwq00000 on 2014/5/15.
 */
public final class BeeReceivedValueArgs extends BeeFrameArgs implements MeasureRecord{
    /**
     * 测量消息解析正则表达式
     */
    private static final Pattern regex = Pattern.compile("ID:(\\S{4})\\s+(-?\\d+(\\.\\d+)?)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS | Pattern.MULTILINE | Pattern.UNIX_LINES);

    private final short batteryValue;
    private final byte pushType;
    private final byte packageId;
    private final String rawValue;
    private final String gageId;
    @NotNull
    private final Date time;
    private final double value;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public BeeReceivedValueArgs(@NotNull BeeFrame frame)  {
        super(frame);
        if (!FrameTypes.isReceivedDataFrame(frame.getBaseFrameType())){
            throw new FrameTypeException("不正确的桢类型");
        }
        pushType =  this.mFrame.frameData[BeeFrame.POSITION_DATA_PART_START];
        packageId = this.mFrame.frameData[BeeFrame.POSITION_DATA_PART_START+2];
        batteryValue = ByteUtils.bytesToShort(this.mFrame.frameData,BeeFrame.POSITION_DATA_PART_START+3);
        byte dataLen = mFrame.getDataLen();
        String message = new String(this.mFrame.frameData,BeeFrame.POSITION_MESSAGE_PART, dataLen - 5, Charset.forName("US-ASCII"));
        Matcher matcher = regex.matcher(message);
        if(matcher.find()){
            gageId = matcher.group(1);
            rawValue = matcher.group(2);
            time = new Date();
            value = Double.parseDouble(rawValue);
        }else {
            throw new FrameTypeException(String.format("消息格式不正确,%s",message));
        }
    }

    @Override
    public String getDescription() {
        return String.format("帧类型:%s\t包ID:%d\t按键:%s\t电量:%d\t读数:%s",
                FrameTypes.toDescription(getFrameType()),
                packageId,
                pushType,
                batteryValue,
                rawValue);
    }

    /**
     * 获取 按键类型
     * @return
     */
    public byte getPushType(){
        return pushType;
    }

    /**
     * 记录时间
     *
     * @return
     */
    @NotNull
    @Override
    public Date getTime() {
        return time;
    }

    /**
     * 测量值
     *
     * @return
     */
    @Override
    public double getValue() {
        return value;
    }

    /**
     * 测量值 读数
     * @return
     */
    public String getRawValue() {
        return rawValue;
    }

    /**
     * 量具Id
     * @return 量具Id
     */
    public String getGageId() {
        return gageId;
    }

    /**
     * 获取 量具电量
     *
     * @return 量具电量
     */
    @Override
    public short getBatteryValue() {
        return batteryValue;
    }

    /**
     * Put a Parcel object back into the pool.  You must not touch
     * the object after this call.
     */
    @Override
    public void recycle() {

    }
}
