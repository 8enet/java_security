package com.javademo.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zl on 16/1/28.
 */
public class SocketStreamServer {

    private boolean running = false;
    private ServerSocket serverSocket;
    private OnMessageListener messageListener;

    private AtomicInteger mConnections = new AtomicInteger(0);

    private static final Executor sThreadPools = Executors.newCachedThreadPool();

    public SocketStreamServer(int port, OnMessageListener messageListener) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        this.messageListener = messageListener;
    }


    public void start() {
        sThreadPools.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket socket = serverSocket.accept();
                        runSocket(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public int getActiveConnetions() {
        return mConnections.get();
    }

    private void runSocket(final Socket socket) {
        sThreadPools.execute(new Runnable() {
            @Override
            public void run() {
                mConnections.incrementAndGet();
                handleReaderServer(socket);
            }
        });
    }

    private void handleReaderServer(final Socket socket) {
        InputStream inputStream = null;
        try {

            inputStream = socket.getInputStream();

            byte[] buff = new byte[8];

            int msgTotalLen = 0; //当前包消息总长度
            int currMsgLen = 0; //已读消息长度

            byte[] remaining = null; //上次读取完成后下个包部分

            ByteBuffer msgBuffer = null; //已读消息部分

            while (true) {

                int len = inputStream.read(buff);

                if (msgTotalLen == 0) {
                    //新的包开始

                    byte[] rF = null;
                    int rlen = 0;

                    if (remaining != null) {
                        //有上次没有读完的部分，合并一起读

                        rlen = remaining.length + len;
                        rF = new byte[rlen];

                        //合并插入最前面
                        System.arraycopy(remaining, 0, rF, 0, remaining.length);
                        System.arraycopy(buff, 0, rF, remaining.length, len);
                        remaining = null;

                    } else {
                        rF = buff;
                        rlen = len;
                    }

                    msgTotalLen = readMsgHeaderLength(rF, 0);

                    if (msgTotalLen + 4 < rlen) {

                        boolean canFullRead = true;

                        int lastLen = 0;
                        while (canFullRead) {

                            msgBuffer = ByteBuffer.allocate(msgTotalLen);
                            msgBuffer.put(Arrays.copyOfRange(rF, 4 + lastLen, msgTotalLen + 4 + lastLen));
                            onRecvMsg(msgBuffer);

                            lastLen += msgTotalLen + 4;

                            if (lastLen + 4 < rlen) {

                                msgTotalLen = readMsgHeaderLength(rF, lastLen);

                                if (lastLen + msgTotalLen + 4 < rlen) {
                                    canFullRead = true;
                                } else {
                                    canFullRead = false;
                                    remaining = Arrays.copyOfRange(rF, lastLen, rlen);
                                }
                            } else {
                                canFullRead = false;

                                remaining = Arrays.copyOfRange(rF, lastLen, rlen);
                            }

                        }

                        msgTotalLen = 0;
                        currMsgLen = 0;

                    } else {

                        msgBuffer = ByteBuffer.allocate(msgTotalLen);
                        msgBuffer.put(Arrays.copyOfRange(rF, 4, rlen));

                        currMsgLen += (rlen - 4);
                    }

                } else {

                    if (currMsgLen + len >= msgTotalLen) {
                        //当前包要读完了

                        int n = msgTotalLen - currMsgLen; //计算当前数据包分割点

                        msgBuffer.put(buff, 0, n);
                        onRecvMsg(msgBuffer);

                        msgTotalLen = 0;
                        currMsgLen = 0;

                        if (n < len) {
                            //保存下次继续读
                            remaining = Arrays.copyOfRange(buff, n, len);
                        } else {
                            remaining = null;
                        }

                    } else {
                        //没有读完，继续
                        currMsgLen += len;
                        msgBuffer.put(buff);
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            mConnections.decrementAndGet();

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    protected void onRecvMsg(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        if (messageListener != null) {
            messageListener.onRecv(byteBuffer.array());
        }
    }

    private int readMsgHeaderLength(byte[] array, int startPosition) {
        return Utils.byteArrayToInt(Arrays.copyOfRange(array, startPosition, startPosition + 4));
    }

    public void stopServer() {
        running = false;
    }

    public interface OnMessageListener {
        void onRecv(byte[] bytes);
    }

}
