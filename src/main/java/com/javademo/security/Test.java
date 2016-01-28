package com.javademo.security;

import org.bouncycastle.jce.provider.*;
import org.bouncycastle.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.*;

/**
 * Created by zl on 15/11/2.
 */
public class Test {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("start");
            
//        final byte[] bytes = a("9de782f75776eba7794a77a67b8a74d0547e03cab828327b1a48771e5209d95d697707a391f5faa8a6638908f161720302bbe8c71b077941d2929147ccaa6349");
//        String v0 = new String(new i(new k("www.lbesec.com", "www.lbesec.com".getBytes())).a(bytes));
//        k k1= new k("www.lbesec.com", v0.getBytes());
//        File file=new File("/Users/zl/apktool/adware2.db");
//        ByteArrayOutputStream baos=new ByteArrayOutputStream();
//
//        copy(new i(k1), file, baos);
//
//        String res=new String(baos.toByteArray());
//
//        System.out.println(res);
//
//
//        FileOutputStream fos=new FileOutputStream("/Users/zl/apktool/adware_real.txt");
//        fos.write(res.getBytes());
//        fos.flush();
//        fos.close();
//
//
//        baos.close();
//        System.out.println(v0);


        //encFile();
        //decFile();

        aaa();
    }

    private static final int ACQUIRE = 0x00000001;

    private static final int ACQUIRING = 0x00000002;

    private static final int ALLOW_MOBILE_DOWNLOAD = 0x00000004;

    private static final int FORBID_MOBILE_DOWNLOAD = 0x00000008;

    private static int sFlags = 0x00000000;


    private static void aaa(){
        sFlags = sFlags & ~FORBID_MOBILE_DOWNLOAD;
        sFlags = sFlags | ALLOW_MOBILE_DOWNLOAD;
        sFlags = sFlags | ACQUIRE;
        sFlags = sFlags | FORBID_MOBILE_DOWNLOAD;
        sFlags = sFlags | FORBID_MOBILE_DOWNLOAD;

        System.out.println(Integer.toString(sFlags,16));


        System.out.println(String.format("0x%08x", 0)+"---");

        System.out.println(Integer.decode(String.format("0x%08x", sFlags)));

        sFlags=Integer.decode(String.format("0x%08x", sFlags));

        System.out.println((sFlags & FORBID_MOBILE_DOWNLOAD) == FORBID_MOBILE_DOWNLOAD);
    }

    private static void encFile() throws Exception{
        String pwd=md5("shafa@!!");

        File file=new File("/Users/zl/develop/github/java_security/src/main/resources/adware.txt");
        Cipher cipher = Cipher.getInstance("DES");
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");


        SecretKey ke =  factory.generateSecret(new DESKeySpec(pwd.getBytes()));

        cipher.init(Cipher.ENCRYPT_MODE, ke);

        FileInputStream fis=new FileInputStream(file);
        CipherInputStream cis=new CipherInputStream(fis,cipher);

        FileOutputStream fos=new FileOutputStream("/Users/zl/develop/github/java_security/src/main/resources/adware_enc.txt");

        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            fos.write(buffer, 0, r);
        }

        cis.close();
        fos.close();
    }

    private static void decFile()throws Exception{
        String pwd=md5("shafa");
        File file=new File("/Users/zl/develop/github/java_security/src/main/resources/adware_enc.txt");
        Cipher cipher = Cipher.getInstance("DES");
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey ke =  factory.generateSecret(new DESKeySpec(pwd.getBytes()));
        cipher.init(Cipher.DECRYPT_MODE, ke);
        FileInputStream fis=new FileInputStream(file);

        ByteArrayOutputStream baos=new ByteArrayOutputStream();

        CipherOutputStream cos=new CipherOutputStream(baos,cipher);
        byte[] buffer = new byte[4096];
        int r;
        while ((r = fis.read(buffer)) >= 0) {
            cos.write(buffer, 0, r);
        }
        cos.close();
        fis.close();

        baos.close();

        System.out.println(baos.toByteArray().length);
        System.out.println(baos.toString());
    }


    public static String md5(String data) {
        return md5(data.getBytes());
    }

    public static String md5(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            for (int i = 0; i < bytes.length; i++) {
                int temp = 0xff & bytes[i];
                if (temp <= 0x0f) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(temp));
            }
        } catch (Exception e) {}

        return sb.toString();
    }


    private static char[] a;

    static {
        a = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
                'f'};
    }

    public static byte[] a(String arg7) {
        int v2;
        int v6 = 16;
        int v1 = 0;
        StringBuilder v4 = new StringBuilder(arg7.length());
        int v0;
        for(v0 = 0; v0 < arg7.length(); ++v0) {
            char v5 = Character.toLowerCase(arg7.charAt(v0));
            v2 = Character.digit(v5, v6) >= 0 ? 1 : 0;
            if(v2 != 0) {
                v4.append(v5);
            }
            else if(!Character.isWhitespace(v5)) {
                throw new IllegalStateException(String.format("Conversion of hex string to array failed. \'%c\' is not a valid hex character",
                        Character.valueOf(v5)));
            }
        }

        if(v4.length() % 2 > 0) {
            v4.append('0');
        }

        byte[] v0_1 = new byte[arg7.length() + 1 >> 1];
        while(v1 < v0_1.length) {
            v2 = v1 << 1;
            v0_1[v1] = ((byte)(Character.digit(v4.charAt(v2 + 1), v6) & 15 | Character.digit(v4.charAt(
                    v2), v6) << 4));
            ++v1;
        }

        return v0_1;
    }

    public static String a(byte[] arg5) {
        int v1 = arg5.length;
        String v0 = null;
        if(arg5 != null) {
            StringBuilder v2 = new StringBuilder(arg5.length * 2);
            int v0_1;
            for(v0_1 = 0; v0_1 < v1; ++v0_1) {
                v2.append(a[(arg5[v0_1] & 240) >>> 4]);
                v2.append(a[arg5[v0_1] & 15]);
            }

            v0 = v2.toString();
        }

        return v0;
    }



    static final class k {
        public String a;
        public byte[] b;
        public Key c;
        public PBEParameterSpec d;

        public k(String arg4, byte[] arg5) throws Exception {
            super();
            this.a = arg4;
            this.b = arg5;
            this.c = SecretKeyFactory.getInstance("PBEWITHSHA256AND128BITAES-CBC-BC", "BC").generateSecret(
                    new PBEKeySpec(this.a.toCharArray()));
            this.d = new PBEParameterSpec(arg5, 512);
        }
    }




    static final class i extends a1 {
        private k b;

        public i(k arg5) throws Exception {
            super();
            this.b = arg5;
            this.a = Cipher.getInstance("PBEWITHSHA256AND128BITAES-CBC-BC", "BC");
            this.a.init(2, this.b.c, this.b.d);
        }
    }


    public static abstract class a1 {
        protected Cipher a;

        public a1() {
            super();
        }

        public final byte[] a(byte[] arg2) throws Exception {
            byte[] v0 = arg2 != null ? this.a.doFinal(arg2) : this.a.doFinal();
            return v0;
        }

        public final byte[] a(byte[] arg3, int arg4) {
            return this.a.update(arg3, 0, arg4);
        }
    }


    static final class g {
        public int a;
        public int b;
        public int c;

        g(byte[] arg4) throws Exception{
            super();
            if(arg4 != null && arg4.length == 4096) {
                ObjectInputStream v0 = new ObjectInputStream(new ByteArrayInputStream(arg4));
                if("LBE_SEC_CRYPTO".equals(v0.readUTF())) {
                    this.a = v0.readInt();
                    this.b = v0.readInt();
                    this.c = v0.readInt();
                    return;
                }
                else {
                    throw new Exception("Invalid header magic");
                }
            }

            throw new Exception("Invalid header length");
        }

        g() {
            super();
        }
    }



    public static int copy(a1 arg11, File arg12, OutputStream arg13) {
        g v2;
        long v9 = 4096;
        int v0 = 5;
        if((arg12.exists()) && (arg12.canRead()) && (arg12.canWrite())) {
            byte[] v3 = new byte[4096];
            try {
                RandomAccessFile v4 = new RandomAccessFile(arg12, "rw");
                if(v4.read(v3) < 0) {
                    return v0;
                }

                try {
                    v2 = new g(v3);
                }
                catch(Exception v0_2) {
                    return 3;
                }

                if(v2.b == 1) {
                    v3 = new byte[v2.c];
                    long v5 = v4.length() - (((long)v2.c));
                    v4.seek(v5);
                    if(v4.read(v3) != v3.length) {
                        return v0;
                    }

                    arg13.write(arg11.a(v3));
                    v4.seek(4096);
                    byte[] v2_1 = new byte[((int)(v5 - v9))];
                    if(v4.read(v2_1) != v2_1.length) {
                        return v0;
                    }

                    arg13.write(v2_1);
                    v4.close();
                    return 1;
                }

                if(v2.b == 2) {
                    int v2_2 = 0;
                    while(true) {
                        int v5_1 = v4.read(v3);
                        if(v5_1 < 0) {
                            break;
                        }

                        arg13.write(arg11.a(v3, v5_1));
                        ++v2_2;
                        if(v2_2 % 256 != 0) {
                            continue;
                        }
                    }

                    arg13.write(arg11.a(null));
                    arg13.flush();
                    return 1;
                }

                v4.close();
                v0 = 9;
            }
            catch(IOException v0_1) {
                v0 = 7;
            }
            catch(Throwable v1) {
            }
        }

        return v0;
    }
}
