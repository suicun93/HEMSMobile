/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author hoang-trung-duc
 */
public class Convert {

    // Log array of hex to console
    public static void printHexArray(byte[] arg) {
        for (int i = 0; i < arg.length; ++i) {
            System.out.println(Integer.toHexString(arg[i] & 0xff));
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        if (s.contains("0x")) {
            s = s.replace("0x", "");
        }
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    // Byte to hex string
    public static String byteToHex(byte arg) {
        return Integer.toHexString(arg & 0xff);
    }

    // Integer to Byte Hex
//    public static byte intToByte(int number) {
//        return Byte.parseByte(Integer.toHexString(number), 16);
//    }
    public static byte intToByte(int number) {
        return (byte) number;
    }

    // Byte Hex to Integer
//    public static int byteToInt(byte number) {
//        return Integer.parseInt(Byte.toString(number), 10);
//    }
    public static int byteToInt(byte number) {
        return number & 0xff;
    }

    // packing an array of 4 bytes to an int, big endian, clean code
    public static int byteArrayToInt(byte[] bytes) {
        byte[] tempByte = null;
        switch (bytes.length) {
            case 0:
                return 0;
            case 1:
                tempByte = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, bytes[0]};
                break;
            case 2:
                tempByte = new byte[]{(byte) 0x00, (byte) 0x00, bytes[0], bytes[1]};
                break;
            case 3:
                tempByte = new byte[]{(byte) 0x00, bytes[0], bytes[1], bytes[2]};
                break;
            case 4:
                tempByte = bytes;
                break;
        }
        return ((tempByte[0] & 0xFF) << 24)
                | ((tempByte[1] & 0xFF) << 16)
                | ((tempByte[2] & 0xFF) << 8)
                | ((tempByte[3] & 0xFF) << 0);
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) (value >> 0)
        };
    }

    public static String getCurrentTime() {
        // "dd MMM yyyy HH:mm:ss"
        Date now = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("dd MM yyyy HH:mm:ss", Locale.ENGLISH);
        dtf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dtf.format(now);
    }
    static byte[] mRatedElectricEnergy = {(byte) 0x00, (byte) 0x00, (byte) 0x13, (byte) 0x88};

    public static void main(String[] args) {
//        System.out.println(byteArrayToInt(new byte[]{(byte) 0x20}));
//printHexArray(intToByteArray(99));
//printHexArray(hexStringToByteArray("0x4050"));
//        System.out.println(byteArrayToInt(mRatedElectricEnergy));
    }
}
