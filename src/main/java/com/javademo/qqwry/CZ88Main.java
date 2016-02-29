package com.javademo.qqwry;


import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

/**
 * Created by zl on 16/2/29.
 */
public class CZ88Main {

    public static void main(String[] args) throws Exception {
        final IPSeeker ipSeeker = IPSeeker.getInstance();


        print(ipSeeker.getIPEntries("上海","电信",500));


//        final byte[] data = getData("http://update.cz88.net/ip/copywrite.rar");
//        if(data != null){
//
//            byte[] cz=new byte[4];
//            System.arraycopy(data,0,cz,0,cz.length);
//
//            System.out.println(new String(cz));
//
//
//            final ByteBuffer order = ByteBuffer.wrap(data,4,data.length-4).order(ByteOrder.LITTLE_ENDIAN);
//
//
//            for (int i=0;i<5;i++){
//                System.out.println(order.getInt());
//            }
//
//        }

    }

    private static void print(Collection collection){
        for (Object item:collection){
            System.out.println(item);
        }
    }

    private static byte[] getData(String url){
        HttpURLConnection connection=null;
        try{
            URL connUrl=new URL(url);

            connection= (HttpURLConnection) connUrl.openConnection();
            connection.addRequestProperty("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
            connection.addRequestProperty("Host",connUrl.getHost());

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                ByteArrayOutputStream baos=new ByteArrayOutputStream(2048);

                byte[] buff=new byte[1024];
                int len=-1;

                InputStream stream = connection.getInputStream();
                while ( (len=stream.read(buff)) != -1){
                    baos.write(buff,0,len);
                }

                return baos.toByteArray();
            }

        }catch (Throwable e){

        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return null;
    }
}
