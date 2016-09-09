package com.javademo.rx;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.*;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zl on 16/9/7.
 */
public class Main1 {

    public static void main(String[] args){
        File file=new File("/Users/zl/Downloads");
        getFile(file).subscribe(new Action1<File>() {
            @Override
            public void call(File file) {
                System.out.println(file);
            }
        });

        getFile(file).collect(new Func0<AtomicLong>() {
            @Override
            public AtomicLong call() {
                return new AtomicLong(0);
            }
        }, new Action2<AtomicLong, File>() {
            @Override
            public void call(AtomicLong aLong, File file) {
                aLong.getAndAdd(file.length());
            }
        }).subscribe(new Action1<AtomicLong>() {
            @Override
            public void call(AtomicLong atomicLong) {
                System.out.println(atomicLong);
            }
        });
        
    }


    public static Observable<File> getFile(File file){
        if(file.isFile()){
            return Observable.just(file);
        }
        return Observable.from(file.listFiles()).concatMap(new Func1<File, Observable<File>>() {
            @Override
            public Observable<File> call(File file) {
                return getFile(file);
            }
        });
    }
}
