package com.javademo.socket;

/**
 * Created by zl on 16/1/28.
 */
public class Utils {

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }


    public static int byteArrayToInt(byte[] array) {
        return array[0] << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF);
    }
}
