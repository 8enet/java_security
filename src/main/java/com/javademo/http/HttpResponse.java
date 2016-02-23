package com.javademo.http;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

/**
 * Created by zl on 16/2/22.
 */
public class HttpResponse {

    public static final String REQUEST_LINE = "REQUEST_LINE";

    static final int LN_CR =0x001; //标记\r开始
    static final int LN_LF =0x002; //标记\n开始
    static final int END_CR_ST =0x004; //标记最后一行\r开始

    static final int BUFF_SIZE=4096;

    static final ResponseConfig DEFAULT_RESO_CONFIG=ResponseConfig.creat().readCookie(false);

    private Socket socket;

    private InputStream inputStream;

    private Map<String, String> headers=new HashMap<>();
    private List<HttpCookie> cookies=null;

    private SoftReference<String> refBody;

    private ResponseConfig responseConfig;


    HttpResponse(Socket socket,ResponseConfig config) throws IOException {
        responseConfig=(config!=null?config:DEFAULT_RESO_CONFIG);

        this.socket=socket;
        inputStream=this.socket.getInputStream();
        readHeader();
    }

    private synchronized void readHeader()throws IOException {
        StringBuilder sb=new StringBuilder();

        int mark= 0x000;
        char c;

        while (true) {
            c = (char) (inputStream.read() & 0xFF);
            if(c == 13){ // \r
                mark = mark | LN_CR;

                if((mark & LN_LF) == LN_LF){
                    //快要结束了
                    mark = mark | END_CR_ST;
                }
            }else if((mark & LN_CR) == LN_CR && c == 10){ // \n
                //一行读取完毕

                String line=sb.toString();
                System.out.println(line);

                if(line.startsWith("HTTP/1.1 /")){
                    headers.put(REQUEST_LINE,line);
                }else {
                    int i = line.indexOf(":");
                    if(i != -1){
                        String key=line.substring(0,i);
                        if (responseConfig.readCookie && "Set-Cookie".equals(key)) {

                            if(cookies == null){
                                cookies=new ArrayList<>();
                            }

                            cookies.addAll(HttpCookie.parse(line.substring(i + 1, line.length()).trim()));
                        } else {
                            headers.put(key, line.substring(i + 1, line.length()).trim());
                        }

                    }else {
                        headers.put(line,line);
                    }
                }

                sb.setLength(0);

                //上一个也是\r\n ,根据http协议规范,返回header已经结束了,下面就要读取body了
                if((mark & END_CR_ST) == END_CR_ST){
                    break;
                }

                //开始正常的读取下一行,重置标记
                mark = mark | LN_CR;
                mark = mark | LN_LF;

            }else {

                //填充读取内容
                sb.append(c);

                mark = mark & ~LN_CR;
                mark = mark & ~END_CR_ST;
                mark = mark & ~LN_LF;
            }

        }


        if(responseConfig.onlyReadHeader){
            finsh();
        }
    }


    public InputStream getContentStream(){
        return inputStream;
    }

    public Map<String, String> getHeaders(){
        return headers;
    }

    public String body() throws IOException{
        if(refBody != null && refBody.get() != null){
            return refBody.get();
        }

        boolean usingGzip="gzip".equals(headers.get("Content-Encoding"));
        boolean chunked="chunked".equals(headers.get("Transfer-Encoding"));

        //需要根据response header 解码数据

        StringBuilder sb=new StringBuilder();

        if(chunked){
            System.out.println("using chunked ");
            inputStream=new ChunkedInputStream(inputStream);
        }

        if(responseConfig.autoUnGzip && usingGzip){
            System.out.println("using gzip ");
            inputStream=new GZIPInputStream(inputStream);
        }

        String contentLengthValue= headers.get("Content-Length");
        int contentLength=-1;
        if(contentLengthValue != null){
            try{
                contentLength=Integer.parseInt(contentLengthValue);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }



        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

        int buffSize= (contentLength> 0 && contentLength<BUFF_SIZE)?contentLength:BUFF_SIZE;

        char[] buff=new char[buffSize];
        int len=-1;

        int readerLen=0;

        System.out.println("http body  "+buffSize);

        //这里也没有处理
        while ( (len=bufferedReader.read(buff)) != -1){
            readerLen+=len;
            sb.append(buff,0,len);

            if(readerLen == contentLength){
                break;
            }
        }

        String body=sb.toString();

        refBody=new SoftReference<String>(body);

        System.out.println("-----body--over-----");
        finsh();
        return body;
    }


    public List<HttpCookie> getCookies(){
        return cookies;
    }

    public int getCode(){
        final String s = headers.get(REQUEST_LINE);
        if(s != null){
            try {
                return Integer.parseInt(s.split(" ")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public String getContentType(){
        return headers.get("Content-Type");
    }


    private void finsh(){
        try {
            if(inputStream != null){
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static class ResponseConfig{
        private boolean readCookie=false;
        private boolean autoUnGzip=true;
        private boolean onlyReadHeader=false;

        ResponseConfig(){

        }
        public static ResponseConfig creat(){
            return new ResponseConfig();
        }

        public ResponseConfig readCookie(boolean r){
            readCookie=r;
            return this;
        }

        public ResponseConfig autoUnGzip(boolean f){
            autoUnGzip=f;
            return this;
        }

        public ResponseConfig onlyReadHeader(boolean f){
            onlyReadHeader=f;
            return this;
        }
    }
}
