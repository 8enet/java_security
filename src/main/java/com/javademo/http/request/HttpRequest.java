package com.javademo.http.request;

import com.javademo.http.HttpResponse;
import com.javademo.socket.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

/**
 * Created by zl on 16/2/22.
 */
public class HttpRequest implements IRequest{

    static final HttpResponse.ResponseConfig DEFAULT_RESO_CONFIG= HttpResponse.ResponseConfig.creat().readCookie(false);

    static final String LINE="\r\n";
    protected String method="GET";
    protected Map<String,String> headers;
    private byte[] postData=null;
    protected URL url;
    private Proxy proxy;
    private int port=-1;

    private HttpResponse.ResponseConfig responseConfig;

    public HttpRequest(URL url) {
        this.url=url;
        String protocol= url.getProtocol();
        if(! "http".equals(protocol) && !"https".equals(protocol)){
            throw new RuntimeException("Unsupport "+protocol+"  protocol ! ");
        }
    }

    public String getPathSegment() {
        String ret="";
        String s= url.toString();
        int pIdx= s.indexOf("//");
        if(pIdx != -1){
            pIdx=pIdx+2;
            try{
                ret=s.substring(s.indexOf("/",pIdx));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return "".equals(ret)?"/":ret;
    }


    public String getMethod() {
        return method;
    }

    public HttpRequest get(){
        this.method="GET";
        return this;
    }

    public HttpRequest post(byte[] postData){
        this.method="POST";
        this.postData=postData;
        return this;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public HttpRequest post(Map<String,String> data){
        if(data != null){
            StringBuilder sb=new StringBuilder();

            final Set<Map.Entry<String, String>> entries = data.entrySet();
            for (Map.Entry<String, String> entry:entries){
                try {
                    sb.append(URLEncoder.encode(entry.getKey(),StandardCharsets.UTF_8.name()))
                            .append('=')
                            .append(URLEncoder.encode(entry.getValue(),StandardCharsets.UTF_8.name())).append('&');
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            char c= sb.charAt(sb.length()-1);
            if(c== '&'){
                sb=sb.deleteCharAt(sb.length()-1);
            }

            return post(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
        return post((byte[]) null);
    }

    public HttpRequest post(){
        return post((byte[])null);
    }

    public String getHost() {
        return url.getHost();
    }

    public int getPort() {
        if(port != -1){
            return port;
        }

        int p = url.getPort();
        port= (p==-1?url.getDefaultPort():p);

        return port;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key,String value) {
        if(headers == null){
            headers=new HashMap<>();
        }
        headers.put(key,value);
    }


    public void addCookie(HttpCookie... cookies){
        if(headers == null){
            headers=new HashMap<>();
        }
        String c= headers.get("Cookie");

        StringBuilder sb=new StringBuilder();
        if(c != null){
            sb.append(c);
        }

        for (HttpCookie cookie:cookies){
            sb.append(cookie.getName()).append('=').append(cookie.getValue()).append(' ');
        }

        int idx= sb.lastIndexOf(" ");
        if(idx != -1){
            sb=sb.deleteCharAt(idx);
        }

        headers.put("Cookie",sb.toString());
    }

    public String getHeaderHost(){
        int p= getPort();
        boolean appendPort=(p!=80 && p != 443);
        //特殊端口才加端口号
        return getHost()+(appendPort?p:"");
    }

    public byte[] asByte(){
        byte[] bytes=null;
        byte[] head=getRequest().getBytes(StandardCharsets.UTF_8);
        if(postData != null){
            bytes=Utils.mergeArray(head,postData);
        }else {
            bytes=head;
        }
        return bytes;
    }

    private String getRequest(){
        StringBuilder sb=new StringBuilder(method);
        sb.append(" ").append(getPathSegment()).append(" HTTP/1.1");
        sb.append(LINE);

        if(headers != null && !headers.isEmpty()){
            if(!headers.containsKey("Host")){
                headers.put("Host",getHeaderHost());
            }
            if("POST".equals(method) && postData != null){
                headers.put("Content-Type","application/x-www-form-urlencoded");
                headers.put("Content-Length",""+postData.length);
            }

            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry:entries){
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(LINE);
            }



        }else {
            sb.append(LINE);
        }

        sb.append(LINE);

        System.out.println(sb);

        return sb.toString();
    }

    public HttpResponse.ResponseConfig getResponseConfig() {
        return responseConfig!=null?responseConfig:DEFAULT_RESO_CONFIG;
    }

    public void setResponseConfig(HttpResponse.ResponseConfig responseConfig) {
        this.responseConfig = responseConfig;
    }

    private boolean isPost(){
        return "POST".equals(method);
    }


    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(asByte());
    }
}
