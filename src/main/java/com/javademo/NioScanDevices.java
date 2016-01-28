package com.javademo;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;


/**
 * 扫描
 * Created by zl on 16/1/26.
 */
public class NioScanDevices {

    private static final int TIMEOUT_SELECT = 300;
    private static long TIMEOUT_CONNECT = 1000 * 1000000; // ns
    private static final long TIMEOUT_RW = 3 * 1000 * 1000000; // ns
    private static final String E_REFUSED = "Connection refused";
    private static final String E_TIMEOUT = "The operation timed out";
    private static final int MAX_READ = 8 * 1024;

    private boolean select = true;
    private Selector selector;

    protected String ipAddr = null;
    private boolean getBanner = false;

    public final static int OPEN = 0;
    public final static int CLOSED = 1;
    public final static int FILTERED = -1;
    public final static int UNREACHABLE = -2;
    public final static int TIMEOUT = -3;

    private void connectSocket(String ip, int port) {
        try {
            SocketChannel socket = SocketChannel.open();
            socket.configureBlocking(false);
            socket.connect(new InetSocketAddress(ip, port));
            ScanData data = new ScanData();
            data.port = port;
            data.ip=ip;
            data.start = System.nanoTime();
            socket.register(selector, SelectionKey.OP_CONNECT, data);
        } catch (Throwable e) {
            //e.printStackTrace();
        }
    }


    public void start() {
        select = true;
        try {
            selector = Selector.open();

            int[] startArray = {172,27,0,1};
            int[] endArray = {172,27,13,254};

            for(int i0 = startArray[0]; i0 <= endArray[0]; ++i0) {
                for(int i1 = startArray[1]; i1 <= endArray[1]; ++i1) {
                    for(int i2 = startArray[2]; i2 <= endArray[2]; ++i2) {
                        for(int i3 = startArray[3]; i3 <= endArray[3]; ++i3) {
                            String ip = i0 + "." + i1 + "." + i2 + '.' + i3;
                            //System.out.println(ip);
                            connectSocket(ip,8899);
                        }
                    }
                }
            }


            while (select && selector.keys().size() > 0) {
                if (selector.select(TIMEOUT_SELECT) > 0) {
                    synchronized (selector.selectedKeys()) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            try {
                                if (!key.isValid()) {
                                    continue;
                                }
                                // States
                                final ScanData data = (ScanData) key.attachment();

                                if (key.isConnectable()) {

                                    if (((SocketChannel) key.channel()).finishConnect()) {
                                        data.state = OPEN;
                                        System.out.println("connect ...."+data);
                                        finishKey(key, OPEN);

                                    }

                                }

                            } catch (ConnectException e) {
                                if (e.getMessage().equals(E_REFUSED)) {
                                    finishKey(key, CLOSED);
                                } else if (e.getMessage().equals(E_TIMEOUT)) {
                                    finishKey(key, FILTERED);
                                } else {
                                    e.printStackTrace();
                                    finishKey(key, FILTERED);
                                }
                            } catch (Exception e) {

                                finishKey(key, FILTERED);

                            } finally {
                                iterator.remove();
                            }
                        }
                    }
                } else {
                    // Remove old/non-connected keys
                    final long now = System.nanoTime();
                    final Set<SelectionKey> keys = selector.keys();

                    for (SelectionKey key:keys){
                        final ScanData data = (ScanData) key.attachment();
                        if (data.state == OPEN && now - data.start > TIMEOUT_RW) {
                            //Log.e(TAG, "TIMEOUT=" + data.port);
                            finishKey(key, TIMEOUT);
                        } else if (data.state != OPEN && now - data.start > TIMEOUT_CONNECT) {
                            finishKey(key, TIMEOUT);
                        }
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSelector();
        }
    }


    public void cancelled() {
        select = false;
    }

    private void closeSelector() {
        try {
            if (selector.isOpen()) {
                synchronized (selector.keys()) {
                    final Set<SelectionKey> keys = selector.keys();

                    for (SelectionKey key:keys){
                        finishKey(key, FILTERED);
                    }
                    selector.close();
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void finishKey(SelectionKey key, int state) {
        finishKey(key, state, null);
    }

    private void finishKey(SelectionKey key, int state, String banner) {
        if(key != null) {

            synchronized (key) {
                if (!key.isValid()) {
                    return;
                }
                try{
                    closeChannel(key.channel());
                    ScanData data = (ScanData) key.attachment();
                    publishProgress(data.port, state, banner);
                    key.attach(null);
                    key.cancel();
                    key = null;
                }catch (Exception e){
                }
            }
        }
    }

    private void publishProgress(Object... values){
        if (values.length == 3) {
            final Integer port = (Integer) values[0];
            final int type = (Integer) values[1];
            if (port != 0) {
                if (type == OPEN) {
                    // Open
                    if (values[2] != null) {
                        System.out.println("open "+(String) values[2]);
                    }

                } else if (type == CLOSED) {
                    // Closed

                } else if (type == UNREACHABLE) {


                    System.out.println("Host Unreachable: " + ipAddr + ":" + port);
                }
                // FIXME: do something ?
                else if (type == TIMEOUT) {
                } else if (type == FILTERED) {
                }
            } else {

                System.out.println("Host Unreachable: " + ipAddr);
            }
        }
    }

    private void closeChannel(SelectableChannel channel) {
        if (channel instanceof SocketChannel) {
            Socket socket = ((SocketChannel) channel).socket();
            try{
                if (!socket.isInputShutdown()){
                    socket.shutdownInput();
                }
            } catch (Exception ex){
            }
            try{
                if (!socket.isOutputShutdown()){
                    socket.shutdownOutput();
                }
            } catch (Exception ex){
            }
            try{
                socket.close();
            } catch (Exception ex){
            }
        }
        try{
            channel.close();
        } catch (Exception ex){
        }
    }

    private static class ScanData {
        protected int state = FILTERED;
        protected int port;
        protected String ip;
        protected long start;
        protected int pass = 0;

        @Override
        public String toString() {
            return "Data{" +
                    "state=" + state +
                    ", port=" + port +
                    ", ip='" + ip + '\'' +
                    '}';
        }
    }
}
