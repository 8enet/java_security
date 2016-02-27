package com.javademo.proxy;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.pubsub.RedisPubSubAdapter;
import com.lambdaworks.redis.pubsub.RedisPubSubConnection;

/**
 * Created by zl on 16/2/27.
 */
public class RedisQueueManager {

    private static final String MQ_CHANNEL="req_queue";

    private static RedisQueueManager redisQueueManager;

    public static RedisQueueManager getInstance(){
        if(redisQueueManager == null){
            synchronized (RedisQueueManager.class){
                if(redisQueueManager == null){
                    redisQueueManager=new RedisQueueManager();
                }
            }
        }
        return redisQueueManager;
    }


    private RedisClient client;
    private RedisPubSubConnection<String, String> connectPubSub;

    //private Jedis jedis;

    private RedisQueueManager(){
        connect();
    }

    private void connect(){

        client=RedisClient.create("redis://192.168.2.102:6379");

//        connectPubSub=client.connectPubSub(new RedisCodec<String, StringBuilder>() {
//
//
//            @Override
//            public String decodeKey(ByteBuffer bytes) {
//                return StandardCharsets.UTF_8.decode(bytes).toString();
//            }
//
//            @Override
//            public StringBuilder decodeValue(ByteBuffer bytes) {
//
//                return new StringBuilder(StandardCharsets.UTF_8.decode(bytes));
//            }
//
//            @Override
//            public byte[] encodeKey(String key) {
//                return encode(key);
//            }
//
//            @Override
//            public byte[] encodeValue(StringBuilder value) {
//                if (value == null) {
//                    return new byte[0];
//                }
//                return String.valueOf(value).getBytes(StandardCharsets.UTF_8);
//            }
//
//            private byte[] encode(String string) {
//
//                if (string == null) {
//                    return new byte[0];
//                }
//                return string.getBytes(StandardCharsets.UTF_8);
//            }
//
//        });

        connectPubSub = client.connectPubSub();


        connectPubSub.addListener(new RedisPubSubAdapter<String, String>(){

            @Override
            public void message(String channel, String message) {
                super.message(channel, message);

                //System.out.println(message);
                System.out.println("\n-------\n\n");
            }


        });


        connectPubSub.subscribe(MQ_CHANNEL);
//


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Jedis jedis = new Jedis("192.168.2.102",6379);
//                jedis.subscribe(new JedisPubSub() {
//                    @Override
//                    public void onMessage(String channel, String message) {
//                        super.onMessage(channel, message);
//                    }
//                },MQ_CHANNEL);
//
//            }
//        }).start();
//
//        jedis=new Jedis("192.168.2.102",6379);

    }

    public void publish(StringBuilder msg) {
        //有待验证,无法接收消息
        connectPubSub.publish(MQ_CHANNEL,msg.toString());

        //jedis.publish(MQ_CHANNEL,msg.toString());
    }

}
