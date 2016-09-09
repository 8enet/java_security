package com.javademo;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Created by zl on 16/8/28.
 */
public class Main3 {

    public static void main(String[] args){
        try {
            ClassPool pool=ClassPool.getDefault();
            pool.insertClassPath("/Users/zl/charles_bak.jar");

            CtClass cc1s = pool.get("com.xk72.charles.License");

            CtMethod ctMethod1 =cc1s.getDeclaredMethod("a",null);
            ctMethod1.setBody("{return true;}");

            CtMethod ctMethod2 = cc1s.getDeclaredMethod("b",null);
            ctMethod2.setBody("{return \"username\";}");

            cc1s.writeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static boolean a(){
        return true;
    }
}
