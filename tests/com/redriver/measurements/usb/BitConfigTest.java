package com.redriver.measurements.usb;

import junit.framework.TestCase;

/**
 * Created by zwq00000 on 2014/7/26.
 */
public class BitConfigTest extends TestCase {
    private static final int BITS_DATA_MASK	= 0X0f00;
    private static final int BITS_DATA_5	= 0X0500;
    private static final int BITS_DATA_6	= 0X0600;
    private static final int BITS_DATA_7	= 0X0700;
    private static final int BITS_DATA_8	= 0X0800;
    private static final int BITS_DATA_9	= 0X0900;

    private static final int BITS_PARITY_MASK= 0X00f0;
    private static final int BITS_PARITY_NONE= 0X0000;
    private static final int BITS_PARITY_ODD	= 0X0010;
    private static final int BITS_PARITY_EVEN= 0X0020;
    private static final int BITS_PARITY_MARK= 0X0030;
    private static final int BITS_PARITY_SPACE= 0X0040;

    private static final int BITS_STOP_MASK	= 0X000f;
    private static final int BITS_STOP_1	= 0X0000;
    private static final int BITS_STOP_1_5	= 0X0001;
    private static final int BITS_STOP_2	= 0X0002;

    public void testBitData() throws Exception{
        int config = 0;
        //case PARITY_NONE:
        assertEquals(config |= (0x00 << 8),BITS_PARITY_NONE);
        //case PARITY_ODD:
        assertEquals( config |= (0x01 << 4),BITS_PARITY_ODD);

        //case PARITY_EVEN:
        assertEquals(config |= (0x02 << 8),BITS_PARITY_EVEN);

        //case PARITY_MARK:
        assertEquals(config |= (0x03 << 8),BITS_PARITY_MARK);

        //case PARITY_SPACE:
        assertEquals( config |= (0x04 << 8),BITS_PARITY_SPACE);

    }
}
