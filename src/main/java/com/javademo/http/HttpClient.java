package com.javademo.http;

import com.javademo.http.request.*;

import javax.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by zl on 16/2/22.
 */
public class HttpClient {

    private Map<String,SocketKeeper> connectionCache=new ConcurrentHashMap<>();


    public HttpResponse request(HttpRequest request) throws IOException {

        String key=request.getHeaderHost();
        HttpResponse.ResponseConfig responseConfig = request.getResponseConfig();
        SocketKeeper socketKeeper = connectionCache.get(key);
        if(responseConfig.isKeepAlive() && socketKeeper != null && socketKeeper.canUse()){
            socketKeeper.using();
            System.err.println("using keep alive");

            request.writeTo(socketKeeper.getSocket().getOutputStream());

            return new HttpResponse(socketKeeper,responseConfig);

        }else {
            if(socketKeeper != null && !socketKeeper.isBusy()){
                socketKeeper.release();

                connectionCache.remove(key);
            }
        }


        Socket socket=obain(request.getPort());

        if(responseConfig.isKeepAlive()){
            socketKeeper=new SocketKeeper(socket);

            connectionCache.put(key,socketKeeper);
        }

        System.err.println("new socket ");

        if(request.getProxy() != null){
            final Proxy proxy = request.getProxy();
            socket.connect(proxy.address());
        }else {
            socket.connect(new InetSocketAddress(request.getHost(),request.getPort()));
        }

        request.writeTo(socket.getOutputStream());

        if(socketKeeper != null){
            return new HttpResponse(socketKeeper,responseConfig);
        }

        return new HttpResponse(socket,responseConfig);
    }


    private Socket obain(int port) throws IOException {
        if(port == 443){
            return SSLSocketFactory.getDefault().createSocket();
        }
        return SocketFactory.getDefault().createSocket();
    }


    public static class SocketKeeper{
        private Socket socket;
        private boolean busy=false;

        public SocketKeeper(Socket socket){
            this.socket=socket;
            busy=true;
        }


        public synchronized Socket getSocket() {
            return socket;
        }

        public synchronized boolean canUse(){
            return (socket != null && !socket.isClosed()) && !busy;
        }

        public synchronized boolean isBusy(){
            return busy;
        }

        public synchronized void using(){
            busy=true;
        }

        public synchronized void onceOver(){
            busy=false;
        }

        public synchronized void release(){
            try {
                if(socket != null){
                    socket.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
