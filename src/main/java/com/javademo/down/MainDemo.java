package com.javademo.down;

/**
 * Created by zl on 16/3/23.
 */
public class MainDemo {
    public static void main(String[] args){
        MyClass myClass = new MyClass();
        StringBuffer buffer = new StringBuffer("hello");
        myClass.changeValue(buffer);
        System.out.println(buffer.toString());
    }

    static class MyClass {

        void changeValue( StringBuffer buffer) {
            buffer=new StringBuffer("sd");
        }
    }
}
