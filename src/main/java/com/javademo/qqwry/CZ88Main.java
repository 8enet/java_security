package com.javademo.qqwry;


import java.io.File;

/**
 * Created by zl on 16/2/29.
 */
public class CZ88Main {

    public static void main(String[] args) throws Exception {

        new CZ88IPDBUpdate(new File("/Users/zl/qqwry.dat")).update();

    }

}
