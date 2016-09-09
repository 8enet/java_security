package com.kotlindemo;

import com.demo.kotlindemo.DemoKt;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zl on 15/11/7.
 */
public class Main {
    public static void main(String[] args){
//        DemoKt.nullCase(null);
//        DemoKt.aa();
//
//        DesignSize s1=new DesignSize(1920,1080);
//        DesignSize s2 = s1.clone();
//        s2.set(111,222);
//
//        System.out.println(s1);
//        System.out.println(s2);
//
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.getDefault());
//        System.out.println(sdf.format(new Date()));

        System.out.println(0xe7);

        System.out.println(Integer.toHexString(231));

        System.out.println(Integer.parseInt("ff",16));

        System.out.println(Integer.valueOf("e7",16));

        Color color=new Color(0xff,69,255);
        
    }

    public static int argb(int alpha,int r,int g,int b){
        int color= ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);

        if(alpha >=0 && alpha <= 100){
            color= (((Math.round(0xff*(alpha/100f))) & 0xFF) << 24) | color;
        }
        return color;
    }

    public static void aaa(){
        System.out.println("java");
    }


    public static class DesignSize implements Cloneable{
        int designUiWidth;
        int designUiHeight;

        public DesignSize(int width,int height){
            if(width <= 0 || height <= 0){
                throw new IllegalArgumentException("designUiWidth or designUiHeight > 0 !!!");
            }
            this.designUiWidth=width;
            this.designUiHeight=height;
        }

        void set(int width,int height){
            this.designUiWidth=width;
            this.designUiHeight=height;
        }

        @Override
        public String toString() {
            return "DesignSize{" +
                    "designUiWidth=" + designUiWidth +
                    ", designUiHeight=" + designUiHeight +
                    '}'+hashCode();
        }

        @Override
        protected DesignSize clone() {
            try {
                return (DesignSize) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
