package com.javademo.http.request;

import com.javademo.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by zl on 16/2/23.
 */
public interface IRequest {

    String getHost();

    int getPort();

    HttpResponse.ResponseConfig getResponseConfig();


    void writeTo(OutputStream outputStream)throws IOException;
}
