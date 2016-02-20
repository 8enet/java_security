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

    public static byte[] mergeArray(byte[]... bytes){
        int count=0;
        for (byte[] arr:bytes){
            count+=arr.length;
        }
        byte[] newBytes=new byte[count];

        int destPos=0;
        for (byte[] arr:bytes){
            System.arraycopy(arr,0,newBytes,destPos,arr.length);
            destPos+=arr.length;
        }
        return newBytes;
    }
}
