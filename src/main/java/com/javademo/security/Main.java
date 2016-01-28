package com.javademo.security;


import com.github.kevinsawicki.http.*;
import org.apache.commons.codec.binary.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.*;
import java.nio.channels.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.*;
import java.util.*;

public class Main {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final String TEST_FILE="/Users/zl/develop/ic_launcher.png";

    public static void main(String[] args)throws Exception {
//        getSecurityInfo();
//
//        messageDigestTest();
//
//        digestStream();
//
//        keypair_test();
//
//        keyspec_test();
//
//        keyStore_test();
//
//        mac_test();
//
//        cipher_test();

        //generatexX509Cert_test();

        //certificate_test();

//        String s="cyVcx4yfciV6bJFvNnqOXgNvUSuaicIzbyDLzT0R+eV7PA+w82mDRmVF6bIt +ukCUBvszxuciK4aNg4jfz7mekBG+2Vrpue5Ho/US6Vv1B+k6AvQvY2WlaWm BByVAco+2ompIunlR5gNe35qHaEgMqmMw6hIf9YZBWhwpPGQBvirwDDMq3T8 QSo2MHjRY0jdnQEz5Xv2q5VQpMV7f/kjGnvk74W2LKkSJ05EJEkNgoBv4hUB uVcK6n9gdi/lX7Qxeaxy6496gYuoGk8DyCWRxG7OJerUDtz50h+E3KMWRLCC 6jaDv4CmTAcYFu7aklcFPzdVSdyUEME46fTpNUefHIzf6lnOR+6HDgY/WQNB WSA+tfqSUqR0gzMHe+H/vb6pDXvjeznGAs6oU3M9hJ4T7okiK+tWsoMge/MN 2NQiLtSTYro8W3DGDono/VZsC/qfApcT9diDz4lY7dE1a3JAepgI4NQH7E4f RuwMdq1lqjR29Ceb30ZbcCnZqTgzWbYjfn7/ib/SPqviN04jAHNi+eEA6did xnxEqmibnAfpeXb8uYNxCmeAEvlE2xI/NRE1xTfprspYeZsWWApp63dh0A==";
//        System.out.println(decryptDES(s));
       // aaaa();
       // aabb();

        aaabbb();
    }

    private static void getSecurityInfo(){

        System.out.println("MessageDigest    "+ Security.getAlgorithms("MessageDigest"));

        System.out.println("Signature    "+Security.getAlgorithms("Signature"));

        System.out.println("KeyStore   "+Security.getAlgorithms("KeyStore"));


        System.out.println("KeyFactory   "+Security.getAlgorithms("KeyFactory"));

        System.out.println("KeyPairGenerator   " + Security.getAlgorithms("KeyPairGenerator"));


        System.out.println("SecureRandom   " + Security.getAlgorithms("SecureRandom"));


        System.out.println("CertificateFactory  " + Security.getAlgorithms("CertificateFactory"));

        System.out.println("CertStore    " + Security.getAlgorithms("CertStore"));


        System.out.println("Mac   " + Security.getAlgorithms("Mac"));

        System.out.println("Cipher   " + Security.getAlgorithms("Cipher"));

        System.out.println("SecretKeyFactory   " + Security.getAlgorithms("SecretKeyFactory"));

        System.out.println("KeyGenerator   " + Security.getAlgorithms("KeyGenerator"));
    }


    private static void messageDigestTest()throws Exception{
        //消息摘要
        MessageDigest messageDigest= MessageDigest.getInstance("MD5");

        byte[] bytes = FileUtils.readFileToByteArray(new File(TEST_FILE));

        byte[] digest = messageDigest.digest(bytes);

        System.out.println(Hex.encodeHexString(digest));

    }


    private static void digestStream() throws NoSuchAlgorithmException, IOException {
        //消息摘要流
        DigestInputStream dis=new DigestInputStream(FileUtils.openInputStream(new File(TEST_FILE)),MessageDigest.getInstance("md5"));

        byte[] buff=new byte[1024];
        while ( dis.read(buff) >0){
        }

        IOUtils.closeQuietly(dis);
        System.out.println(Hex.encodeHexString(dis.getMessageDigest().digest()));
    }



    private static void keyspec_test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // KeySpec  EncodedKeySpec
        //  X509EncodedKeySpec   public key,公钥规范
        // PKCS8EncodedKeySpec   private key,私钥规范

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("rsa"); //密钥对生成器
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        KeyFactory keyFactory = KeyFactory.getInstance("rsa");

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //下同
    }

    private static void keypair_test() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // key 下面又三大接口 PrivateKey,PublicKey,SecretKry
        //PrivateKey,PublicKey  对应非对称加密接口  rsa,dsa,ec
        //SecretKry 对应对称加密接口  des

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("rsa"); //密钥对生成器
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        //签名
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update("abc".getBytes());
        byte[] sign = signature.sign();
        System.out.println("sign  "+Hex.encodeHexString(sign));

        //验证
        signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(keyPair.getPublic());
        signature.update("abc".getBytes());
        boolean verify = signature.verify(sign);
        System.out.println(verify);

    }


    private static void testKey(KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update("abc".getBytes());
        byte[] sign = signature.sign();

        signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(keyPair.getPublic());
        signature.update("abc".getBytes());

        boolean verify = signature.verify(sign);
        System.out.println("verify  result  "+verify);
    }


    private static void signature_test(){
    }


    private static void keyStore_test() throws KeyStoreException, URISyntaxException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, SignatureException, InvalidKeyException {
        //密钥库
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        keyStore.load(FileUtils.openInputStream(new File(ClassLoader.getSystemClassLoader().getResource("demo.keystore").toURI())), "123456".toCharArray());

        java.security.cert.Certificate certificate = keyStore.getCertificate("mytestkey");

        PrivateKey mytestkey = (PrivateKey) keyStore.getKey("mytestkey", "123456".toCharArray());

        System.out.println(Hex.encodeHexString(mytestkey.getEncoded()));

        testKey(new KeyPair(certificate.getPublicKey(), mytestkey));


    }

    private static void certificate_test() throws CertificateException, IOException, KeyStoreException, URISyntaxException, NoSuchAlgorithmException {
        final CertificateFactory certificateFactory = CertificateFactory.getInstance("x.509");
        final java.security.cert.Certificate certificate = certificateFactory.generateCertificate(FileUtils.openInputStream(new File("src/main/resources/demo.cert")));

        System.out.println("certificate_test   " + certificate);

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        keyStore.load(FileUtils.openInputStream(new File(ClassLoader.getSystemClassLoader().getResource("demo.keystore").toURI())), "123456".toCharArray());

        final KeyPair keyPair = KeyPairGenerator.getInstance("rsa").generateKeyPair();

        keyStore.setKeyEntry("aaa_bbb", keyPair.getPrivate(), "abc123".toCharArray(), new Certificate[]{certificate});

        System.out.println(Collections.list(keyStore.aliases()));



    }


    private static void mac_test() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, InvalidKeySpecException {
        //比md5更加安全的消息摘要
        SecretKeySpec secretKey=new SecretKeySpec("abcd".getBytes(),"HMACMD5");
        //final SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();

        //DESKeySpec desKeySpec=new DESKeySpec("".getBytes());

        final Mac hmacmd5 = Mac.getInstance("HMACMD5");
        hmacmd5.init(secretKey);

        hmacmd5.update("abc".getBytes());

        final byte[] bytes = hmacmd5.doFinal();

        System.out.println(" crypto  " + Hex.encodeHexString(bytes));

    }

    private static void cipher_test() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //非对称 加密 解密
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        final KeyPair keyPair = keyPairGenerator.generateKeyPair();

        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        final byte[] bytes = cipher.doFinal("abcd".getBytes());
        System.out.println("加密 byte result  " + bytes.length);


        final Cipher cipher2 = Cipher.getInstance("RSA");
        cipher2.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        final byte[] bytes1 = cipher2.doFinal(bytes);
        System.out.println("解密 result  "+new String(bytes1));

        cipher2_test();
    }

    private static void cipher2_test() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        //对称加密解密
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        final SecretKey secretKey = keyGenerator.generateKey();

        String text="aasdsadasd";

        final Cipher cipher = Cipher.getInstance("DES");  //同一个对象可以使用多次，重新使用前要init()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] bytes = cipher.doFinal(text.getBytes());
        System.out.println("加密   " + Hex.encodeHexString(bytes));

//        final Cipher cipher2 = Cipher.getInstance("DES");
//        cipher2.init(Cipher.DECRYPT_MODE, secretKey);
//        final byte[] bytes1 = cipher2.doFinal(bytes);
//        System.out.println("解密结果   " + new String(bytes1));

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        bytes=cipher.doFinal(bytes);
        System.out.println("解密结果   " + new String(bytes));
    }

    /**
     * 生成 X509 证书
     * @return
     */
    private static void generatexX509Cert_test() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = kpg.generateKeyPair();
            PublicKey pubKey = keyPair.getPublic();
            PrivateKey priKey = keyPair.getPrivate();

            final byte[] signatureData ;
            try {
                Signature signature = Signature.getInstance("SHA1withRSA");
                signature.initSign(priKey);
                signature.update(pubKey.getEncoded());
                signatureData = signature.sign();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            SubjectPublicKeyInfo publicKeyInfo=SubjectPublicKeyInfo.getInstance(new ASN1InputStream(pubKey.getEncoded()).readObject());
            X509v3CertificateBuilder certificateBuilder=new X509v3CertificateBuilder(new X500Name("cn=root,ou=sdsd,o=abcd"), BigInteger.probablePrime(32,new Random()),new Date(),new Date(2020,1,1),new X500Name("cn=root"),publicKeyInfo);
            final X509CertificateHolder holder = certificateBuilder.build(new ContentSigner() {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();

                @Override
                public AlgorithmIdentifier getAlgorithmIdentifier() {

                    return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
                }

                @Override
                public OutputStream getOutputStream() {
                    return buf;
                }

                @Override
                public byte[] getSignature() {
                    try {
                        buf.write(signatureData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return signatureData;
                }
            });

            byte[] certBuf = holder.getEncoded();
            X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(certBuf));

            System.out.println(certificate);

            File outFile= new File("src/main/resources/demo.cert");
            FileUtils.writeByteArrayToFile(outFile,certBuf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String decryptDES(String decryptString) throws Exception {

        byte[] v0 =Base64.decodeBase64(decryptString);
        IvParameterSpec v4 = new IvParameterSpec(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        SecretKeySpec v3 = new SecretKeySpec("MARKETCC".getBytes(), "DES");
        Cipher v1 = Cipher.getInstance("DES/CBC/PKCS5Padding");
        v1.init(2, ((Key)v3), ((AlgorithmParameterSpec)v4));
        return new String(v1.doFinal(v0), "utf-8");
    }


    private static void aaaa(){

        if(true){
            try {
                aa3();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }



        //BufferedOutputStream bos=new BufferedOutputStream(null);
        //bos.write();


        try {
            InputStream is=new FileInputStream("/Users/zl/com.fanshi.tvbrowser.6944f4b8192709886e0d07fc50969d98.shafa");

            File outFile= new File("src/main/resources/copy.data");

            //FileOutputStream fos=new FileOutputStream(outFile);


            RandomAccessFile raf=new RandomAccessFile(outFile,"rw");
            raf.seek(raf.length());


            byte[] data=new byte[1024*10];

            int len=-1;

            ByteBuffer buffer = ByteBuffer.allocate(data.length*100);

            //FileChannel fileChannel = fos.getChannel();


            while ((len = is.read(data,0,data.length)) != -1){

//                if(buffer.position()+len >= buffer.capacity()){
//                    System.out.println("write ");
//                    buffer.flip();
//                    fileChannel.write(buffer);
//                    buffer.clear();
//                }
//
//                buffer.put(data,0,len);
//
//                System.out.println("put "+len);


                raf.write(data,0,len);

            }



//            System.out.println(buffer.position());
//
//            if(buffer.position() > 0){
//                buffer.flip();
//                fileChannel.write(buffer);
//            }
//
//            buffer.clear();
//
//
//
//            fileChannel.force(true);
//
//            fos.flush();
//            fos.getFD().sync();

            IOUtils.closeQuietly(is);
//            IOUtils.closeQuietly(fileChannel);
//            IOUtils.closeQuietly(fos);

            IOUtils.closeQuietly(raf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static void aa2() throws Exception {





        final HttpRequest request = HttpRequest.get("http://apps.sfcdn.org/apk/com.fanshi.tvbrowser.6944f4b8192709886e0d07fc50969d98.shafa");


        int count = 2958466;
        int pos = 2058466;

        boolean seek=false;


        //md5 6944f4b8192709886e0d07fc50969d98

        File outFile= new File("src/main/resources/copy2.data");
        if(outFile.exists() && outFile.length()<count){

            request.header("Range", "bytes=" + (pos+1) + "-");
            System.out.println("range "+pos);

            seek=true;

            //outFile.delete();
        }else {
            request.header("Range", "bytes=0-"+pos);

            seek=false;
        }

        seek=true;


//        if(f.exists()){
//            f.delete();
//        }

        long st=System.currentTimeMillis();


        try (
                InputStream stream = request.stream();
                FileOutputStream fos = new FileOutputStream(outFile,true);
                FileInputStream fis=new FileInputStream(outFile);
                FileChannel fisChannel=fis.getChannel();
                FileChannel fosChannel = fos.getChannel()
        ) {

            long contentLength=request.contentLength();

            System.out.println(" content length "+contentLength);


            byte[] buff = new byte[1024 * 100];
            int len = -1;


            ByteBuffer buffer = ByteBuffer.allocate(buff.length);

            if(seek){
                System.out.println("seek "+pos);
                //fosChannel.position(pos);

                fisChannel.position(count);

                ByteBuffer allocate = ByteBuffer.allocate(8);
                fisChannel.read(allocate);

                System.out.println("get Long "+allocate.getLong(0));

                //fosChannel.position(pos);

            }else {

                System.out.println("fosChannel  position "+fosChannel.position());

                ByteBuffer allocate = ByteBuffer.allocate(8);
                fosChannel.position(count);

                System.out.println("fosChannel  position "+fosChannel.position());

                allocate.putLong(0,count);
                fosChannel.write(allocate,count);

                fosChannel.position(0);

                System.out.println("seek "+seek);
            }


            fosChannel.position(0);

            System.out.println("fosChannel  position "+fosChannel.position());





//            while ((len = stream.read(buff)) != -1) {
//
//                if (buffer.position() + len >= buffer.capacity()) {
//                    buffer.flip();
//                    fosChannel.write(buffer);
//                    buffer.clear();
//                }
//
//                buffer.put(buff, 0, len);
//
//            }
//
//            if (buffer.position() > 0) {
//                buffer.flip();
//                fosChannel.write(buffer);
//            }
//
//            buffer.clear();
//            fosChannel.force(true);


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }

    }


    private static void aa3(){

        final HttpRequest request = HttpRequest.get("http://apps.sfcdn.org/apk/com.fanshi.tvbrowser.6944f4b8192709886e0d07fc50969d98.shafa");


        int count = 2958466;
        int pos = 2058466;

        boolean seek=false;
        int cp=0;


        //md5 6944f4b8192709886e0d07fc50969d98

        File outFile= new File("src/main/resources/copy2.data");
        if(outFile.exists() && outFile.length()<count){

            request.header("Range", "bytes=" + (pos+1) + "-");
            System.out.println("range "+pos);

            seek=true;

            cp=pos;

            //outFile.delete();
        }else {
            request.header("Range", "bytes=0-"+pos);

            seek=false;
            cp=0;
        }


//        if(f.exists()){
//            f.delete();
//        }

        long st=System.currentTimeMillis();


        try (
                InputStream stream = request.stream();
                FileOutputStream fos = new FileOutputStream(outFile,true);
                FileChannel fosChannel = fos.getChannel();
                ReadableByteChannel readableByteChannel = Channels.newChannel(stream);
        ) {

            long contentLength=request.contentLength();

            System.out.println(" content length "+contentLength);


            fosChannel.transferFrom(readableByteChannel,outFile.length(),contentLength);


            fosChannel.force(true);

//            while ((len = stream.read(buff)) != -1) {
//
//                if (buffer.position() + len >= buffer.capacity()) {
//                    buffer.flip();
//                    fosChannel.write(buffer);
//                    buffer.clear();
//                }
//
//                buffer.put(buff, 0, len);
//
//            }
//
//            if (buffer.position() > 0) {
//                buffer.flip();
//                fosChannel.write(buffer);
//            }
//
//            buffer.clear();
//            fosChannel.force(true);


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }
    }



    private static void aaa4(){

        File file=new File("src/main/resources/copy2.data");

//        if(file.exists()){
//            file.delete();
//        }

        try(
                FileOutputStream fos=new FileOutputStream(file,true);
                FileChannel channel= fos.getChannel();
        ){

            int size=10240;
            ByteBuffer buff=ByteBuffer.allocate(1024);
            //channel.write(buff,size+buff.capacity());


            buff.putLong(0,Long.MAX_VALUE);
            buff.flip();
            final int write = channel.write(buff, 29);

            System.out.println("write "+write);

            channel.force(true);


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private static void aabb(){
        String str="abcde1234567890";

        byte[] data=str.getBytes();
        int len=data.length;

        byte[] buff=new byte[3];
        int pos=0;


        ByteBuffer byteBuffer=ByteBuffer.allocate(5);

        while (pos != len){
            int c=Math.min(buff.length,len-pos);
            System.arraycopy(data,pos,buff,0,c);



            if(byteBuffer.position()+c >= byteBuffer.capacity()){
                System.out.println(new String(byteBuffer.array()));

                byteBuffer.clear();
            }

            byteBuffer.put(buff,0,c);

            pos+=c;
        }

    }

    private static void aaabbb(){
        int count=3;
        float[] start={0,7};
        float[] end={9,15};

        final float[] floats = splitLine(start, end, 0.3333f);
        System.out.println(Arrays.toString(floats));

        getLineXYData(-10d,-7d,9d,15d);


        double[] start1={-10,-7};
        double[] end1={9,15};
        getLineXYData(start1,end1,3);





    }

    private static float[] splitLine(float[] start,float[] end,float scale){
        return new float[]{(start[0]+end[0]*scale)/(scale+1),(start[1]+end[1]*scale)/(scale+1)};
    }

    /**
     *
     * @param start [x1,y1]
     * @param end   [x2,y2]
     * @param n     n段,n>1
     */
    private static void getLineXYData(double[] start, double[] end, int n){
        double[] tmp = new double[2];
        for (int i = 1; i < n; i++) {
            tmp[0] = end[0] - start[0];
            tmp[1] = end[1] - start[1];
            double k = i / (double) n;
            double[] floats = new double[]{start[0] + tmp[0] * k, start[1] + tmp[1] * k};
            System.out.println(i + "   " + Arrays.toString(floats));
        }
    }


    public static float[] devideLine2(
            float[] start, //线段起点坐标
            float[] end, //线段终点坐标
            int n, //线段分的份数
            int i //求第i份在线段上的坐标（i为0和n时分别代表起点和终点坐标）
    )
    {
        if(n==0){//如果n为零，返回起点坐标
            return start;
        }
        //求起点到终点的向量
        float[] ab=new float[]{end[0]-start[0], end[1]-start[1], end[2]-start[2]};
        //求向量比例
        float vecRatio=i/(float)n;
        //求起点到所求点的向量
        float[] ac=new float[]{ab[0]*vecRatio, ab[1]*vecRatio, ab[2]*vecRatio};
        //所求坐标
        float x=start[0]+ac[0];
        float y=start[1]+ac[1];
        float z=start[2]+ac[2];
        //返回线段的n等分点坐标
        return new float[]{x, y, z};
    }


    private static ArrayList<Map<String, Double>> getLineXYData(Double Ax, Double Ay, Double Bx, Double By){
        ArrayList<Map<String, Double>> list = new ArrayList<>();
        int point_number = 2;
        Boolean pdnumber = true;
        for(int i=0;i<point_number;i++){
            Map<String, Double> map = new HashMap<>();
            Double x ;
            Double y ;
            if(pdnumber){
                y= (2*Ax+Bx)/3;
                x = (2*Ay+By)/3;
                pdnumber = false;
            }else{
                y = (Ax+2*Bx)/3;
                x = (Ay+2*By)/3;
            }

            map.put("x", x);
            map.put("y", y);
            list.add(map);
        }

        for(int t=0;t<list.size();t++){
            System.out.println("该三等分点为：C"+t+"("+list.get(t).get("x")+","+list.get(t).get("y")+")");
        }
        return list;
    }

}
