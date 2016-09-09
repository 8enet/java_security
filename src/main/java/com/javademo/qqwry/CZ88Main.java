package com.javademo.qqwry;


import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by zl on 16/2/29.
 */
public class CZ88Main {

    public static void main(String[] args) throws Exception {

        final IPSeeker ips = IPSeeker.getInstance();
        ips.load(new File("/Users/zl/qqwry.dat"));

        System.out.println(ips.getIPLocation("219.138.146.250"));

        System.out.println(0x00FFFFFF);

        

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                File newF=new File("/Users/zl/qqwry.datnew");
//                if(new CZ88IPDBUpdate(newF).update()){
//                    ips.load(newF);
//
//                    System.out.println(ips.getIPLocation("180.166.76.34"));
//
//                    System.out.println(ips.getIPLocation("180.134.92.34"));
//                }
//
//
//            }
//        }).start();

    }

}
