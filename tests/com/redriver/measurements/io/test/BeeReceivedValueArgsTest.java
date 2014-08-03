package com.redriver.measurements.io.test;

import com.redriver.measurements.io.BeeFrame;
import com.redriver.measurements.io.BeeReceivedValueArgs;
import com.redriver.measurements.io.FrameTypes;
import junit.framework.TestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zwq00000 on 2014/5/15.
 */
public class BeeReceivedValueArgsTest extends TestCase {

    public void  testConstructor()  {
        byte[] data = TestUtils.getFrameData(); //receiveMsg.getBytes() ;
        BeeFrame frame = new BeeFrame((byte)(FrameTypes.RX_FRAME | FrameTypes.RECEIVED_DATA),data);
        BeeReceivedValueArgs args = new BeeReceivedValueArgs(frame);
        assertEquals(args.getGageId(),"0003");
        assertEquals(args.getRawValue(),"12.123");

        frame = TestUtils.genReceiveBeeFrame("0001", "1");
        args = new BeeReceivedValueArgs(frame);
        assertEquals(args.getRawValue(),"1");
        assertEquals(args.getGageId(),"0001");

        frame = TestUtils.genReceiveBeeFrame("ABCD", "1");
        args = new BeeReceivedValueArgs(frame);
        assertEquals(args.getRawValue(),"1");
        assertEquals(args.getGageId(),"ABCD");

        frame = TestUtils.genReceiveBeeFrame("FF00", "1");
        args = new BeeReceivedValueArgs(frame);
        assertEquals(args.getRawValue(),"1");
        assertEquals(args.getGageId(),"FF00");
    }

    public void  testParseMessage()  {
        BeeFrame frame = TestUtils.genReceiveBeeFrame("0001", "-123.12");
        BeeReceivedValueArgs args = new BeeReceivedValueArgs(frame);
        assertEquals(args.getGageId(), "0001");
        assertEquals(args.getRawValue(),"-123.12");
    }

    public void testMessageParse(){
        String receiveMsg = "ID:0003 -12.123\r\n";
        Pattern regex = Pattern.compile("ID:(\\d{4})\\s+(-?\\d+\\.?\\d+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS | Pattern.MULTILINE | Pattern.UNIX_LINES);
        Matcher matcher = regex.matcher(receiveMsg);
        if(matcher.find()){
            String str1 = matcher.group(1);
            String str2 = matcher.group(2);
            System.out.println(String.format("%s\t%s",str1,str2));
        }


        //DebugPattern("\bID:(\\W+)",receiveMsg);
        //DebugPattern("\bID:(\\S+)(-?\\d+\\.\\d+)",receiveMsg);
        //DebugPattern("ID:(\\W+)\\s?(-?\\d+\\.\\d+)",receiveMsg);
        //DebugPattern(".*(-?\\d+\\.\\d+)",receiveMsg);
        //DebugPattern("ID:(\\W+)\\s*(-?\\d+\\.\\d+)",receiveMsg);

    }

    private void  DebugPattern(String regular,String input){
        System.out.println(String.format("Pattern:%s\tinput:%s",regular,input));
        Pattern pattern = Pattern.compile(regular,Pattern.CASE_INSENSITIVE);
        DebugPattern(pattern,input);
        DebugMatcher(pattern,input);
    }

    private void DebugPattern(Pattern pattern,String input){
        System.out.println(String.format("Debug Pattern:%s",input));
        String[] splits = pattern.split(input);
        System.out.println(String.format("split count:%d",splits.length));
        for (int i = 0; i < splits.length; i++) {
            System.out.println(splits[i]);
        }
    }

    private void DebugMatcher(Pattern pattern,String input){
        System.out.println(String.format("Debug Pattern:%s",input));
        Matcher matcher = pattern.matcher(input);
        System.out.println(String.format("groupCount:%d",matcher.groupCount()));
        System.out.println(String.format("matches:%b",matcher.matches()));
        int i = 0;
        while(matcher.find()) {
            System.out.print(String.format(" group start:%d\tend:%d",matcher.start(),matcher.end()));
            System.out.println(String.format("group(%d)",i++, matcher.group()));
        }
    }
}
