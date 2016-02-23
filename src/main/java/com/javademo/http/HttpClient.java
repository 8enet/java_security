package com.javademo.http;

import com.javademo.http.request.IRequest;

import javax.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;

/**
 * Created by zl on 16/2/22.
 */
public class HttpClient {

    public HttpResponse request(IRequest request) throws IOException {

        SocketFactory socketFactory = request.getPort() == 443 ? SSLSocketFactory.getDefault() : SocketFactory.getDefault();

        Socket socket=socketFactory.createSocket();
        socket.connect(new InetSocketAddress(request.getHost(),request.getPort()));

        //socket.connect(new InetSocketAddress("127.0.0.1",8888));

        request.writeTo(socket.getOutputStream());

        return new HttpResponse(socket,request.getResponseConfig());
    }




}
