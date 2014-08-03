package com.hoho.android.usbserial;

/**
* Created by zwq00000 on 2014/7/26.
*/
public class SerialPortParameters {

    /**
     * 波特率
     */
    public final int baudRate;
    /**
     * 数据位
     */
    public final int dataBits;
    /**
     * 停止位
     */
    public final int stopBits;

    /**
     * 奇偶校验
     */
    public final int parity;

    public SerialPortParameters(int baudRate, int dataBits, int stopBits, int parity) {
        this.baudRate = quantiseBaudRate(baudRate);
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    public static final SerialPortParameters DefaultSettings = new SerialPortParameters(115200, 8, 1, 0);

    /**
     * Quantises the baud rate as per AN205 Table 1
     * @param baud
     * @return
     */
    public static int quantiseBaudRate(int baud) {
        if (baud <= 300)        return 300;
        else if (baud <= 600)  return 600;
        else if (baud <= 1200) return 1200;
        else if (baud <= 1800) return 1800;
        else if (baud <= 2400) return 2400;
        else if (baud <= 4000) return 4000;
        else if (baud <= 4803) return 4800;
        else if (baud <= 7207) return 7200;
        else if (baud <= 9612) return 9600;
        else if (baud <= 14428) return 14400;
        else if (baud <= 16062) return 16000;
        else if (baud <= 19250) return 19200;
        else if (baud <= 28912) return 28800;
        else if (baud <= 38601) return 38400;
        else if (baud <= 51558) return 51200;
        else if (baud <= 56280) return 56000;
        else if (baud <= 58053) return 57600;
        else if (baud <= 64111) return 64000;
        else if (baud <= 77608) return 76800;
        else if (baud <= 117028) return 115200;
        else if (baud <= 129347) return 128000;
        else if (baud <= 156868) return 153600;
        else if (baud <= 237832) return 230400;
        else if (baud <= 254234) return 250000;
        else if (baud <= 273066) return 256000;
        else if (baud <= 491520) return 460800;
        else if (baud <= 567138) return 500000;
        else if (baud <= 670254) return 576000;
        else if (baud <= 1053257) return 921600;
        else if (baud <= 1474560) return 1228800;
        else if (baud <= 2457600) return 1843200;
        else return 3686400;
    }
}
