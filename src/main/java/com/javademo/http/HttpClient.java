package com.javademo.http;

import javax.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.*;

/**
 * Created by zl on 16/2/22.
 */
public class HttpClient {

    public HttpResponse request(HttpRequest request) throws IOException {

        SocketFactory socketFactory = request.getPort() == 443 ? SSLSocketFactory.getDefault() : SocketFactory.getDefault();

        Socket socket=socketFactory.createSocket();
        socket.connect(new InetSocketAddress(request.getHost(),request.getPort()));
        socket.getOutputStream().write(request.asByte());

        return new HttpResponse(socket,request.getResponseConfig());
    }




}
