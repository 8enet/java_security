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


    private static void client(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //数据包格式为 内容长度(int)+内容

                    Socket socket=new Socket("127.0.0.1",44560);
                    final OutputStream stream = socket.getOutputStream();

                    Random random=new Random();

                    while (true){


                        String s=getString(random.nextInt(10)+1);

                        System.out.println("client out: "+s);

                        stream.write(packageData(s));

                        TimeUnit.SECONDS.sleep(random.nextInt(5));
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }).start();
    }

    public static void main(String[] args){

        try {

            ServerSocket serverSocket=new ServerSocket(44560);

            client();
            client();
            client();

            while (true){
                final Socket accept = serverSocket.accept();
                handleServer(accept);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void handleServer(final Socket socket) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    InputStream inputStream = socket.getInputStream();

                    byte[] buff=new byte[8];


                    int plen=0; //当前包消息总长度
                    int clen=0; //已读消息长度

                    byte[] tmp=null; //上次读取完成后下个包部分

                    ByteBuffer buffer=null; //已读消息部分

                    while (true){

                        int len = inputStream.read(buff);

                        if(plen == 0){

                            byte[] rF=null;
                            int rlen=0;

                            if(tmp != null){
                                //有上次没有读完的部分，合并一起读

                                rlen=tmp.length+len;

                                //System.out.println("rlen "+rlen);
                                rF=new byte[rlen];

                                //合并插入最前面
                                System.arraycopy(tmp,0,rF,0,tmp.length);
                                System.arraycopy(buff,0,rF,tmp.length,len);
                                tmp=null;

                            }else {
                                rF=buff;
                                rlen=len;
                            }


                            plen=byteArrayToInt(Arrays.copyOfRange(rF,0,4));

                            if(plen <0 || plen >200){
                                System.err.println("plen  -->>> "+plen);
                                throw new RuntimeException("plen  -->>> "+plen);
                            }

                            //System.out.println("plen "+plen+"   rlen "+rlen+"   ");


                            if(plen +4 < rlen){


                                boolean canFullRead=true;

                                int lastLen=0;
                                while (canFullRead){

                                    //System.out.println("lastLen   "+lastLen+"   plen "+plen);

                                    buffer = ByteBuffer.allocate(plen);

                                    buffer.put(Arrays.copyOfRange(rF, 4+lastLen, plen+4+lastLen));

                                    buffer.flip();

                                    recvPackage(buffer.array());


                                    lastLen+=plen+4;

                                    //System.err.println("last len "+lastLen);


                                    if(lastLen +4 < rlen){

                                        plen=byteArrayToInt(Arrays.copyOfRange(rF,lastLen,lastLen+4));

                                        //System.err.println("read next len "+plen);

                                        if(lastLen+plen+4 < rlen){
                                            canFullRead=true;
                                        }else {
                                            canFullRead=false;
                                            tmp=Arrays.copyOfRange(rF,lastLen,rlen);
                                        }
                                    }else {
                                        canFullRead=false;


                                        //System.err.println("read over lastLen "+lastLen+"   rFL  "+rlen);

                                        tmp=Arrays.copyOfRange(rF,lastLen,rlen);
                                    }

                                }


                                plen=0;
                                clen=0;

                            }else {


                                buffer = ByteBuffer.allocate(plen);

                                buffer.put(Arrays.copyOfRange(rF, 4, rlen));

                                clen += rlen - 4;

                            }

                        }else {

                            //System.out.println("clen "+clen+"  len "+len);

                            //当前包要读完了
                            if(clen+len >= plen){

                                int n= plen-clen; //计算当前数据包分割点

                                //System.out.println("end n "+n);

                                buffer.put(buff,0,n);

                                plen=0;
                                clen=0;

                                buffer.flip();
                                recvPackage(buffer.array());


                                if(n  < len){
                                    //保存下次继续读
                                    tmp=Arrays.copyOfRange(buff,n,len);
                                }else {
                                    tmp=null;
                                }

                            }else {
                                //没有读完，继续

                                clen+=len;
                                buffer.put(buff);
                            }

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                    System.exit(1);
                }

            }
        }).start();
    }


    /**
     * 数据包
     * @param data
     */
    private static void recvPackage(byte[] data){
        System.err.println("recvPackage --> "+new String(data));
    }


    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }


    public static int byteArrayToInt(byte[] array){
        return array[0] << 24 | (array[1] & 0xFF) << 16 | (array[2] & 0xFF) << 8 | (array[3] & 0xFF);
    }

    public static byte[] packageData(String msg){
        byte[] msgData=msg.getBytes();
        int len=msgData.length;

        byte[] data=new byte[len+4];


        System.arraycopy(intToByteArray(len),0,data,0,4);

        System.arraycopy(msgData,0,data,4,len);

        //System.out.println("len "+byteArrayToInt(Arrays.copyOfRange(data,0,4))+"    msg "+new String(Arrays.copyOfRange(data,4,data.length)));

        return data;
    }


    public static String getString(int count){
        StringBuilder sb=new StringBuilder();
        Random random=new Random();
        for (int i=0;i<count;i++){
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    //static final String[] CHARS={"df","09","vn","d","f","gv","5t","23","t","h","fg","6","a","df","lo"};

    static final String[] CHARS={"df","09","vn","凤凰","kldj","0oeore","地方","23","dkjfd09","跌幅v","密码","76743","a","df","开机动画"};
}
