package com.javademo.http.request;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by zl on 16/2/23.
 */
public class MultipartFileRequest extends HttpRequest{


    public static class FilePart{
        private String name;
        private File file;
        private String contentType;

        public FilePart(String name,File file,String contentType){
            this.name= name;
            this.file=file;
            this.contentType=contentType;
        }

        public FilePart(String name,File file){
            this(name,file,"application/octet-stream");
        }


    }

    private static final String HEADER_CONTENT_DISPOSITION="Content-Disposition";

    private static final String STR_CR_LF = "\r\n";
    private static final byte[] CR_LF = STR_CR_LF.getBytes();

    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final String boundary;


    private List<FilePart> fileParts;
    private Map<String,String> params;


    public MultipartFileRequest(URL url) {
        super(url);


        final StringBuilder buf = new StringBuilder("----------");
        final Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }

        boundary = buf.toString();

        method="POST";
    }

    public void addFile(FilePart file){
        if(fileParts == null){
            fileParts =new ArrayList<>();
        }
        fileParts.add(file);
    }

    public void addPart(String key,String value){
        if(params == null){
            params=new HashMap<>();
        }
        params.put(key,value);
    }

    private String createContentDisposition(String key) {

              return   HEADER_CONTENT_DISPOSITION +
                        ": form-data; name=\"" + key + "\"" + STR_CR_LF;
    }

    private String createContentDisposition(String key, String fileName) {
        return
                HEADER_CONTENT_DISPOSITION +
                        ": form-data; name=\"" + key + "\"" +
                        "; filename=\"" + fileName + "\"" + STR_CR_LF;
    }

    private String normalizeContentType(String type) {
        return type == null ? "application/octet-stream" : type;
    }

    private String createContentType(String type) {
        return "Content-Type: " + normalizeContentType(type) + STR_CR_LF;
    }


    private void send(OutputStream outputStream)throws IOException{
        StringBuilder sb=new StringBuilder(method);
        sb.append(" ").append(getPathSegment()).append(" HTTP/1.1");
        sb.append(LINE);

        final byte[] boundaryEnd=("--"+boundary + "--" + STR_CR_LF).getBytes();


        //写入字符串参数
        StringBuilder paramsSb=new StringBuilder();
        if(params != null){
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry:entries){
                paramsSb.append("--").append(boundary).append(STR_CR_LF);
                paramsSb.append(createContentDisposition(entry.getKey()));
                paramsSb.append(createContentType("text/plain; charset=UTF-8"));
                paramsSb.append(STR_CR_LF);
                paramsSb.append(entry.getValue());
                paramsSb.append(STR_CR_LF);
            }
        }

        int fileLength=0;
        StringBuilder fileSb=new StringBuilder();
        if(fileParts != null){
            for (FilePart filePart: fileParts){

                fileSb.append("--").append(boundary).append(STR_CR_LF);
                fileSb.append(createContentDisposition(filePart.name,filePart.file.getName()));
                fileSb.append(createContentType(filePart.contentType));
                fileSb.append("Content-Transfer-Encoding: binary").append(STR_CR_LF);
                fileSb.append(STR_CR_LF);
                //file
                fileSb.append(STR_CR_LF);
                fileLength+=filePart.file.length();
            }
        }

        //总长度，post 表单参数+文件+结束行长度
        byte[] headerDataSize=fileSb.append(paramsSb).toString().getBytes(StandardCharsets.UTF_8);

        int contentLength=headerDataSize.length+fileLength+boundaryEnd.length;


        if(headers != null && !headers.isEmpty()){
            //根据http 1.1规范，必须加入Host
            if(!headers.containsKey("Host")){
                headers.put("Host",getHeaderHost());
            }

            headers.put("Content-Type","multipart/form-data; boundary="+boundary);
            headers.put("Content-Length",""+contentLength);

            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry:entries){
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(STR_CR_LF);
            }

        }else {
            sb.append(LINE);
        }
        sb.append(STR_CR_LF);


        System.out.println("header  \n"+sb);

        //发送header
        outputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));


        System.out.println("params  \n"+paramsSb);

        //发送表单参数
        outputStream.write(paramsSb.toString().getBytes(StandardCharsets.UTF_8));

        if(fileParts != null){
            //发文件
            for (FilePart filePart: fileParts){
                fileSb.setLength(0);
                fileSb.append("--").append(boundary).append(STR_CR_LF);
                fileSb.append(createContentDisposition(filePart.name,filePart.file.getName()));
                fileSb.append(createContentType(filePart.contentType));
                fileSb.append("Content-Transfer-Encoding: binary").append(STR_CR_LF);
                fileSb.append(STR_CR_LF);

                System.out.println("fileSb  \n "+fileSb);

                outputStream.write(fileSb.toString().getBytes(StandardCharsets.UTF_8));
                uploadFile(filePart.file,outputStream);

                outputStream.write(CR_LF);
            }
        }else {
            outputStream.write(CR_LF);
        }

        //最后边界
        outputStream.write(boundaryEnd);

    }


    private void uploadFile(File file,OutputStream outputStream){
        FileInputStream fis=null;
        try {
            fis=new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = fis.read(buffer))!=-1){
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fis != null){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        send(outputStream);
    }
}
