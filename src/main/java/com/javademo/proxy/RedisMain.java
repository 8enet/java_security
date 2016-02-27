package com.javademo.proxy;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.pubsub.*;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * Created by zl on 16/2/26.
 */
public class RedisMain {

    public static void main(String[] args) throws InterruptedException {
        RedisClient client = RedisClient.create("redis://192.168.2.102:6379");
        RedisStringsConnection<String, String> connection = client.connect();

        System.out.println(connection.get("aa"));


        Jedis jedis = new Jedis("192.168.2.102", 6379);

        System.out.println(jedis.get("aa"));

    }

}
