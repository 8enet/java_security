package com.javademo.ws;

import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zl on 16/4/7.
 */
public class WSMain {

    public static void main(String[] args) throws Exception {
        //wsTest();

        System.out.println(0>0);

    }


    private static void lockTest() throws BrokenBarrierException, InterruptedException {

        final CyclicBarrier cyclicBarrier=new CyclicBarrier(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("---working--");
                    TimeUnit.SECONDS.sleep(3);
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


        System.out.println("over");
    }

    private static void wsTest()throws Exception{
        WebSocketFactory factory = new WebSocketFactory();
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null,new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }},null);

        factory.setSSLSocketFactory(context.getSocketFactory());

        WebSocket ws = factory.createSocket("wss://127.0.0.1:8443/control").connect();

        ws.addListener(new WebSocketAdapter(){
            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                super.onTextMessage(websocket, text);

                System.out.println("recv  --> "+text);
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                super.onConnectError(websocket, exception);
                exception.printStackTrace();
            }


            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                super.onConnected(websocket, headers);
                System.out.println(websocket);
            }
        });

        Gson gson=new Gson();

        OrderModel model=new OrderModel();

//
//        //查询手机号
//        ws.sendText(gson.toJson(model));

        model.reset();
        model.setModule(OrderModel.Module.USER);
        model.setAction(OrderModel.Action.ADD);
        //model.addParams("id","14");

        System.out.println(gson.toJson(model));

        ws.sendText(gson.toJson(model));

//        model.reset();
//        model.setAction("user");
//        model.addParams("id","1");
//        ws.sendText(gson.toJson(model));
    }


    private static void httpTest()throws Exception{
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null,new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }},null);


        OkHttpClient client=new OkHttpClient.Builder()
                .sslSocketFactory(context.getSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                .build();

        Callback callback=new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        };

        Request request=new Request.Builder().url("https://127.0.0.1:8443/query?phone=13800138000").build();
        client.newCall(request).enqueue(callback);

        request=new Request.Builder().url("https://127.0.0.1:8443/query?phone=13100131000").build();
        client.newCall(request).enqueue(callback);

        request=new Request.Builder().url("https://127.0.0.1:8443/query?phone=13200132000").build();
        client.newCall(request).enqueue(callback);

        request=new Request.Builder().url("https://127.0.0.1:8443/query?phone=13300133000").build();
        client.newCall(request).enqueue(callback);


        Thread.currentThread().join();
    }


}
