package org.tiger.guava.common.redission;

import org.redisson.Redisson;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

public class ObjectBucket {

    public static void main(String[] args) {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient client = Redisson.create(config);

        RBucket<String> bucket = client.getBucket("stringObject");

        bucket.trySet(new String("git"));

        RBinaryStream stream = client.getBinaryStream("anyStream");
        byte[] content = new byte[] {};
        stream.set(content);
        
        
    }
}
