package com.javademo.http;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Created by zl on 16/2/20.
 */
public class Main3 {
    public static final String REQUEST_LINE = "REQUEST_LINE";

    static final int LN_CR =0x001; //标记\r开始
    static final int LN_LF =0x002; //标记\n开始
    static final int END_CR_ST =0x004; //标记最后一行\r开始

    public static void main(String[] args){
        ioHttp();
    }



    private static void ioHttp(){
        try {
            //这样设置代理貌似无效!
//            System.setProperty("socksProxyHost","127.0.0.1");
//            System.setProperty("socksProxyPort","8889");
//

            String host="www.oschina.net";
            HttpRequest request=new HttpRequest(host,80);
            request.get();
            request.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
            request.addHeader("Accept-Encoding","gzip, deflate, sdch");
            //request.addHeader("Connection","close");

            String req=request.getRequest();

            Proxy proxy=new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("127.0.0.1",8889));

            Socket socket=new Socket(); //可以设置socks代理
            socket.connect(new InetSocketAddress(request.getHost(),request.getPort()));

            socket.getOutputStream().write(req.getBytes(StandardCharsets.UTF_8));

            InputStream inputStream = socket.getInputStream();

            Map<String, String> headers=new HashMap<>();

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
                            headers.put(line.substring(0,i),line.substring(i+1,line.length()).trim());
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


            boolean usingGzip="gzip".equals(headers.get("Content-Encoding"));
            boolean chunked="chunked".equals(headers.get("Transfer-Encoding"));

            //需要根据response header 解码数据

            if(chunked){
                System.out.println("using chunked ");
                inputStream=new ChunkedInputStream(inputStream);
            }

            if(usingGzip){
                System.out.println("using gzip ");
                inputStream=new GZIPInputStream(inputStream);
            }

            System.out.println("http body \n");

            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

            char[] buff=new char[4096];
            int len=-1;

            //这里也没有处理
            while ( (len=bufferedReader.read(buff)) != -1){
                sb.append(buff,0,len);
            }

            System.out.println(sb);

            inputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void nioHttp() throws IOException {

        String host="www.csdn.net";
        HttpRequest request=new HttpRequest(host,80);
        request.get();
        request.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
        request.addHeader("Accept-Encoding","gzip, deflate, sdch");
        //request.addHeader("Connection","close");

        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(request.getHost(),request.getPort())); //如果要用代理调试,可以直接连接代理服务器,记得加Host header
        socketChannel.configureBlocking(false);


        System.out.println(socketChannel);

        HttpHandle httpHandle=new HttpHandle();
        httpHandle.mWriteBuff=ByteBuffer.wrap(request.getRequest().getBytes(StandardCharsets.UTF_8));

        socketChannel.register(selector, SelectionKey.OP_CONNECT|SelectionKey.OP_READ|SelectionKey.OP_WRITE,httpHandle);

        while (selector.keys().size() > 0){
            if(selector.select(30000) > 0){

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    try {
                        SelectionKey key = iterator.next();
                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isConnectable()) {
                            System.out.println("conn " + key.channel());
                        } else if (key.isReadable()) {
                            if (key.channel() instanceof SocketChannel) {
                                HttpHandle handle = (HttpHandle) key.attachment();
                                handle.read((SocketChannel) key.channel());
                            }
                        } else if (key.isWritable()) {

                            if (key.channel() instanceof SocketChannel) {
                                HttpHandle handle = (HttpHandle) key.attachment();
                                handle.send((SocketChannel) key.channel());
                            }

                        }
                    }catch (Throwable e){
                        e.printStackTrace();
                    }finally {
                        iterator.remove();

                    }
                }
            }
        }

    }


    static class HttpHandle{
        static final int LN_ST=0x001;
        static final int ENDLN_ST=0x002;
        static final int LAST_ST=0x004;


        private ByteBuffer mReadBuff=ByteBuffer.allocate(4096);

        private ByteBuffer mWriteBuff;
        private int mWriteRemaing=-1;

        private int mask = 0x000;
        private StringBuffer sb=new StringBuffer();

        private boolean headerReadEnd=false;
        private boolean readingBody=false;

        private Map<String,String> headers=new HashMap<>();

        private boolean usingGzip=false;
        private boolean chunked=false;

        public void send(SocketChannel channel) throws IOException {
            if(mWriteRemaing == -1){
                synchronized (this) {
                    mWriteRemaing = mWriteBuff.capacity();
                }
            }

            if(mWriteRemaing > 0){
                mWriteRemaing-=channel.write(mWriteBuff);
            }
        }

        public synchronized void  read(SocketChannel channel)throws IOException{
            mReadBuff.clear();
            channel.read(mReadBuff);
            mReadBuff.flip();

            try{


                if(!headerReadEnd){
                    readHeader(mReadBuff);
                }


                if(mReadBuff.hasRemaining()){
                    //不知道用nio怎么读出gzip/chunck 流了 T_T
                    //难道用ByteArrayOutputStream全部写进去?但是也不知道流有多长..

                    System.out.println("body ...");

                }

            }catch (Throwable e){
                e.printStackTrace();
            }
        }


        private void readHeader(ByteBuffer buffer){

            char c1;
            while (buffer.hasRemaining()) {
                c1 = (char) (buffer.get() & 0xff);

                if(c1 == '\r'){
                    mask = mask | LN_ST;

                    if((mask & ENDLN_ST) == ENDLN_ST){
                        //header end
                        mask = mask | LAST_ST;
                    }
                }else if((mask & LN_ST) == LN_ST && c1 == '\n'){
                    //line end

                    String line=sb.toString();
                    System.out.println(line);

                    if(line.startsWith("HTTP/1.1 ")){
                        headers.put(REQUEST_LINE,line);
                    }else {
                        int i = line.indexOf(":");
                        if(i != -1){
                            headers.put(line.substring(0,i),line.substring(i+1,line.length()).trim());
                        }
                    }

                    sb.setLength(0);

                    if((mask & LAST_ST) == LAST_ST){
                        headerReadEnd=true;
                        initUsingStream();
                        break;
                    }

                    mask = mask | LN_ST;
                    mask = mask | ENDLN_ST;

                }else {
                    //header
                    sb.append(c1);

                    mask = mask & ~ LN_ST;
                    mask = mask & ~ LAST_ST;
                    mask = mask & ~ ENDLN_ST;
                }

            }
        }

        private synchronized void initUsingStream(){
            usingGzip="gzip".equals(headers.get("Content-Encoding"));
            chunked="chunked".equals(headers.get("Transfer-Encoding"));
        }

    }


    static class HttpRequest{
        static final String LINE="\r\n";
        private String host;
        private int port;
        private String method;
        private String pathSegment;
        private Map<String,String> headers;

        public HttpRequest(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getPathSegment() {
            return pathSegment!=null?pathSegment:"/";
        }

        public void setPathSegment(String pathSegment) {
            this.pathSegment = pathSegment;
        }

        public String getMethod() {
            return method;
        }

        public void get(){
            this.method="GET";
        }

        public void post(){
            this.method="POST";
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
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

        String getRequest(){
            StringBuilder sb=new StringBuilder(method);
            sb.append(" ").append(getPathSegment()).append(" HTTP/1.1");
            sb.append(LINE);

            if(headers != null && !headers.isEmpty()){
                if(!headers.containsKey("Host")){
                    headers.put("Host",host);
                }
                Set<Map.Entry<String, String>> entries = headers.entrySet();
                for (Map.Entry<String, String> entry:entries){
                    sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(LINE);
                }
            }else {
                sb.append(LINE);
            }

            sb.append(LINE);
            return sb.toString();
        }

    }

}