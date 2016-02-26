package com.javademo.proxy;

import com.lambdaworks.redis.*;
import com.lambdaworks.redis.pubsub.*;

/**
 * Created by zl on 16/2/26.
 */
public class RedisMain {

    public static void main(String[] args){
        RedisClient client = RedisClient.create("redis://localhost");
        RedisStringsConnection<String, String> connection = client.connect();

        System.out.println(connection.get("aa"));

        final RedisPubSubConnection<String, String> stringStringRedisPubSubConnection = client.connectPubSub();

    }

}
