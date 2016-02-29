package com.javademo.qqwry;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Inflater;

/**
 * 纯真ip数据库文件更新
 * Created by zl on 16/2/29.
 */
public class CZ88IPDBUpdate {

    private File outFile;

    public CZ88IPDBUpdate(File outFile) {
        this.outFile = outFile;
    }

    /**
     * 更新
     * @return true 表示更新成功
     */
    public boolean update() {
        final byte[] copywrite = getData("http://update.cz88.net/ip/copywrite.rar");
        if (copywrite != null) {


            final ByteBuffer order = ByteBuffer.wrap(copywrite, 4, copywrite.length - 4).order(ByteOrder.LITTLE_ENDIAN);

            int version = order.getInt();
            int unknown1 = order.getInt();
            int size = order.getInt();
            int unknown2 = order.getInt();
            int key = order.getInt();

            byte[] date = getData("http://update.cz88.net/ip/qqwry.rar");

            if (date != null && date.length == size) {

                byte[] head = new byte[512];
                for (int i = 0; i < head.length; i++) {
                    key = (key * 0x805 + 1) & 0xff;
                    head[i] = (byte) (date[i] ^ key);
                }

                System.arraycopy(head, 0, date, 0, head.length);

                decompress(date);

                return true;
            }

        }

        return false;
    }

    private void decompress(byte[] data) {
        Inflater decompresser = null;
        FileOutputStream fos = null;

        try {
            decompresser = new Inflater();
            decompresser.reset();
            decompresser.setInput(data);

            fos = new FileOutputStream(outFile);

            byte[] buff = new byte[16384];

            int len = -1;
            while (!decompresser.finished()) {
                len = decompresser.inflate(buff);
                if(len != -1){
                    fos.write(buff, 0, len);
                }
            }
            fos.flush();
            fos.getFD().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (decompresser != null) {
                decompresser.end();
            }
        }
    }


    private byte[] getData(String url) {
        HttpURLConnection connection = null;
        try {
            URL connUrl = new URL(url);

            connection = (HttpURLConnection) connUrl.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
            connection.addRequestProperty("Host", connUrl.getHost());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);

                byte[] buff = new byte[1024];
                int len = -1;

                InputStream stream = connection.getInputStream();
                while ((len = stream.read(buff)) != -1) {
                    baos.write(buff, 0, len);
                }

                return baos.toByteArray();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }
}
