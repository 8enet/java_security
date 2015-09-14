package com.javademo.security;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import sun.security.x509.X509CertImpl;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class Main {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final String TEST_FILE="/Users/zl/develop/ic_launcher.png";

    public static void main(String[] args)throws Exception {
        getSecurityInfo();

        messageDigestTest();

        digestStream();

        keypair_test();

        keyspec_test();

        keyStore_test();

        mac_test();

        cipher_test();

        //generatexX509Cert_test();

        certificate_test();
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


}
