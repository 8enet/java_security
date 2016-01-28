package com.javademo;

import com.javademo.socket.*;
import rx.*;
import rx.functions.*;
import rx.schedulers.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by zl on 16/1/25.
 */
public class Main2 {


    public static void main(String[] args){

        nioScan();

        if(true){
            return;
        }

        boolean ok = false;

        String ip="172.27.13.80";
        SocketChannel channel=null;
        try {

            channel= SocketChannel.open();
            channel.configureBlocking(false);
            final boolean b = channel.connect(new InetSocketAddress(ip, 5555));
            final Selector selector = Selector.open();
            channel.register(selector,SelectionKey.OP_CONNECT);

            while (true){
                if(selector.select(3000) == 0){
                    continue;
                }

                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    try {
                        if(key.isConnectable()){


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            try {
                if(channel != null){
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void nioScan(){

       // test4();

//        NioScanDevices scanDevices=new NioScanDevices();
//        scanDevices.start();
//
//        System.out.println("over ");


        try {


            try {



                for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                    NetworkInterface iface = (NetworkInterface) ifaces.nextElement();

                    System.out.println(iface.toString());

                    System.out.println(Arrays.toString(iface.getHardwareAddress()));

                }


                if(true){
                    return;
                }

                final InetAddress addr = NetworkUtils.getLocalHostLANAddress();
                if(addr instanceof Inet4Address){

                    int ipAddress=NetworkUtils.inetAddressToInt((Inet4Address)addr);
                    int len= NetworkUtils.getNetworkPrefixLength(addr);

                    int netmask=NetworkUtils.prefixLengthToNetmaskInt(len);
                    int start = ipAddress & netmask;
                    int end = ~netmask | ipAddress;

                    int[] startArray = NetworkUtils.intToArray(start);
                    int[] endArray = NetworkUtils.intToArray(end);

                    String log=Arrays.toString(startArray)+"   "+Arrays.toString(endArray);

                    System.out.println(log);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            NetworkInterface ni = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost());
            InetAddress hostAddress = InetAddress.getByName("224.0.0.1");

            int port=9898;

            DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(port))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            dc.configureBlocking(true);

            MembershipKey key = dc.join(hostAddress, ni);


            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (true) {
                if (key.isValid()) {
                    byteBuffer.clear();
                    InetSocketAddress sa = (InetSocketAddress) dc.receive(byteBuffer);
                    byteBuffer.flip();

                    System.out.println("Multicast received from " + sa.getHostString()+readerByteBuffer(byteBuffer));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String readerByteBuffer(ByteBuffer buffer){
        final byte[] array = buffer.array();
        final int arrayOffset = buffer.arrayOffset();
        final byte[] ofRange = Arrays.copyOfRange(array, arrayOffset + buffer.position(),
                arrayOffset + buffer.limit());
        return new String(ofRange);
    }




    private static void test4(){

        MulticastServer server= null;
        try {
            server = new MulticastServer("224.0.0.1",9898,9898);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final MulticastServer myserver=server;

        if(myserver != null) {

            final Scheduler.Worker worker = Schedulers.io().createWorker();
            worker.schedule(new Action0() {

                @Override
                public void call() {
                    try {
                        myserver.send("MulticastServer  send " + System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    worker.schedule(this, 3, TimeUnit.SECONDS);
                }
            }, 3, TimeUnit.SECONDS);
        }

    }


}
