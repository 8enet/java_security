package com.javademo.socket;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * Created by zl on 16/1/26.
 */
public class MulticastServer {

    private DatagramChannel mDatagramChannel;

    private InetAddress mGroupAddr;
    private SocketAddress mTargetAddr;

    private ByteBuffer mByteBuffer=ByteBuffer.allocate(1024);



    public MulticastServer(String targetHost, int targetPort, int localPort)throws Exception {
        mGroupAddr=InetAddress.getByName(targetHost);

        NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

        mDatagramChannel=DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(localPort))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);

        mDatagramChannel.configureBlocking(false);

        mDatagramChannel.join(mGroupAddr, ni);

        mTargetAddr=new InetSocketAddress(mGroupAddr,targetPort);
    }

    public void send(String str) throws IOException {
        if(mDatagramChannel != null && mDatagramChannel.isOpen() ){
            mByteBuffer.clear();
            mByteBuffer.put(str.getBytes());
            mByteBuffer.flip();
            mDatagramChannel.send(mByteBuffer,mTargetAddr);
        }
    }

    public void close() throws Exception {
        if(mDatagramChannel != null && mDatagramChannel.isOpen()){
            mByteBuffer.clear();
            mDatagramChannel.disconnect().close();
        }
    }

}
