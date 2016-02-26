package com.javademo.proxy;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.pubsub.*;
import io.netty.handler.codec.http.*;
import io.netty.util.*;
import net.lightbody.bmp.*;
import net.lightbody.bmp.filters.*;
import net.lightbody.bmp.mitm.*;
import net.lightbody.bmp.mitm.manager.*;
import net.lightbody.bmp.util.*;

import java.io.*;
import java.util.*;

/**
 * 简单http/https 代理服务器
 * Created by zl on 16/2/26.
 */
public class ProxyMain {

    /**
     * 查看证书信息 openssl x509 -in /Users/zl/certificate.cer -text
     */

    static final String X509_CERT="/Users/zl/certificate.cer";
    static final String PRIVATE_KEY="/Users/zl/private-key.pem";
    static final String PASSWORD="asdf!@321";


    static final int PROXY_PORT=8099;

    /**
     * 生成根证书
     */
    private static void rootCertificateGenerator(){
        RootCertificateGenerator rootCertificateGenerator = RootCertificateGenerator.builder().build();
        rootCertificateGenerator.saveRootCertificateAsPemFile(new File(X509_CERT));
        rootCertificateGenerator.savePrivateKeyAsPemFile(new File(PRIVATE_KEY), PASSWORD);
    }

    public static void main(String[] args){

        //先生成证书，导入certificate.cer 文件到系统或浏览器，只要生成一次并导入即可
        //rootCertificateGenerator();


        //然后将证书加载到
        CertificateAndKeySource existingCertificateSource=new PemFileCertificateSource(new File(X509_CERT),new File(PRIVATE_KEY), PASSWORD);

        ImpersonatingMitmManager mitmManager = ImpersonatingMitmManager.builder()
                .rootCertificateSource(existingCertificateSource)
                .build();


        BrowserMobProxy proxy = new BrowserMobProxyServer(PROXY_PORT);

        proxy.setMitmManager(mitmManager);



        RedisClient client = RedisClient.create("redis://localhost");
        final RedisPubSubConnection<String, String> connectPubSub = client.connectPubSub();
        connectPubSub.addListener(new RedisPubSubAdapter<String, String>(){

            @Override
            public void message(String channel, String message) {
                super.message(channel, message);

                System.out.println(message);
            }
        });

        connectPubSub.subscribe("req");

        final RedisAsyncConnection<String, String> asyncConnection = client.connectAsync();


        proxy.addRequestFilter(new RequestFilter() {
            @Override
            public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents, HttpMessageInfo messageInfo) {

                final HttpHeaders headers = request.headers();
                if(headers != null){
                    final String accept = headers.get("Accept");
                    if(accept != null && (accept.contains("application"))){
                        StringBuilder sb=new StringBuilder();

                        sb.append(request.getUri());
                        sb.append("\n");
                        final List<Map.Entry<String, String>> entries = headers.entries();
                        for (Map.Entry<String, String> entry:entries){
                            System.out.println(entry.getKey()+":"+entry.getValue());
                            sb.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
                        }

                        asyncConnection.publish("req",sb.toString());
                    }
                }

                return null;
            }
        });

        proxy.addResponseFilter(new ResponseFilter() {
            @Override
            public void filterResponse(HttpResponse response, HttpMessageContents contents, HttpMessageInfo messageInfo) {

                //System.out.println(messageInfo.getOriginalUrl());

//                final HttpHeaders headers = response.headers();
//
//                if(contents.isText()){
//
//                    if(headers != null){
//                        final List<Map.Entry<String, String>> entries = headers.entries();
//                        for (Map.Entry<String, String> entry:entries){
//                            System.out.println(entry.getKey()+":"+entry.getValue());
//                        }
//                    }
//                    //System.out.println(contents.getTextContents());
//                }
            }
        });

        //二级代理
        //proxy.setChainedProxy(new InetSocketAddress("127.0.0.1",8090));


        proxy.start();

    }





}
