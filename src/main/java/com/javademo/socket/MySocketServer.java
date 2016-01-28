package com.javademo.socket;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * socket 拆包
 * Created by zl on 16/1/28.
 */
public class MySocketServer {

    private static final int PORT=44560;



    public static void main(String[] args) {

        try {

            new SocketStreamServer(PORT, new SocketStreamServer.OnMessageListener() {
                @Override
                public void onRecv(byte[] bytes) {
                    System.err.println("onRecv -->  "+new String(bytes));
                }
            }).start();


            startClient();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void startClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //数据包格式为 内容长度(int)+内容

                    Socket socket = new Socket("127.0.0.1", PORT);
                    final OutputStream stream = socket.getOutputStream();

                    Random random = new Random();


                    Scanner scanner=new Scanner(System.in);

                    while (true) {


//                        String s = getString(random.nextInt(10) + 1);
//                        System.out.println("client out: " + s);

                        System.out.println("input:");
                        String s= scanner.nextLine();

                        stream.write(encodeData(s.getBytes())); //发包协议

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static byte[] encodeData(byte[] msgData) {
        int len = msgData.length;

        byte[] data = new byte[len + 4];
        System.arraycopy(Utils.intToByteArray(len), 0, data, 0, 4);
        System.arraycopy(msgData, 0, data, 4, len);

        //System.out.println("len "+byteArrayToInt(Arrays.copyOfRange(data,0,4))+"    msg "+new String(Arrays.copyOfRange(data,4,data.length)));
        return data;
    }


    public static String getString(int count) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    //static final String[] CHARS={"df","09","vn","d","f","gv","5t","23","t","h","fg","6","a","df","lo"};

    static final String[] CHARS = {"df", "09", "vn", "凤凰", "kldj", "0oeore", "地方", "23", "dkjfd09", "跌幅v", "密码", "76743", "a", "df", "开机动画"};
}
