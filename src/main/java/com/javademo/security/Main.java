package com.javademo.security;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Main {

    public static final String TEST_FILE="/Users/zl/develop/ic_launcher.png";

    public static void main(String[] args)throws Exception {
        getSecurityInfo();

        messageDigestTest();

        digestStream();

        keypair_test();

        keyspec_test();

        keyStore_test();
    }

    private static void getSecurityInfo(){
        System.out.println("MessageDigest    "+ Security.getAlgorithms("MessageDigest"));

        System.out.println("Signature    "+Security.getAlgorithms("Signature"));

        System.out.println("KeyStore   "+Security.getAlgorithms("KeyStore"));


        System.out.println("KeyFactory   "+Security.getAlgorithms("KeyFactory"));


        System.out.println("KeyPairGenerator   "+Security.getAlgorithms("KeyPairGenerator"));


        System.out.println("SecureRandom   "+Security.getAlgorithms("SecureRandom"));


        System.out.println("CertificateFactory  "+ Security.getAlgorithms("CertificateFactory"));

        System.out.println("CertStore    "+Security.getAlgorithms("CertStore"));
    }


    private static void messageDigestTest()throws Exception{
        MessageDigest messageDigest= MessageDigest.getInstance("MD5");

        byte[] bytes = FileUtils.readFileToByteArray(new File(TEST_FILE));

        byte[] digest = messageDigest.digest(bytes);

        System.out.println(Hex.encodeHexString(digest));

    }


    private static void digestStream() throws NoSuchAlgorithmException, IOException {
        DigestInputStream dis=new DigestInputStream(FileUtils.openInputStream(new File(TEST_FILE)),MessageDigest.getInstance("md5"));

        byte[] buff=new byte[1024];
        while ( dis.read(buff) >0){
        }

        IOUtils.closeQuietly(dis);
        System.out.println(Hex.encodeHexString(dis.getMessageDigest().digest()));
    }



    private static void keyspec_test() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // KeySpec  EncodedKeySpec
        //  X509EncodedKeySpec   public key
        // PKCS8EncodedKeySpec   private key

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


        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update("abc".getBytes());
        byte[] sign = signature.sign();
        System.out.println("sign  "+Hex.encodeHexString(sign));


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

        keyStore.load(FileUtils.openInputStream(new File(ClassLoader.getSystemClassLoader().getResource("demo.keystore").toURI())),"123456".toCharArray());

        java.security.cert.Certificate certificate = keyStore.getCertificate("mytestkey");

        PrivateKey mytestkey = (PrivateKey) keyStore.getKey("mytestkey", "123456".toCharArray());

        System.out.println(Hex.encodeHexString(mytestkey.getEncoded()));

        testKey(new KeyPair(certificate.getPublicKey(),mytestkey));
    }

}
