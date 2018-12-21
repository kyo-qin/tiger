package org.tiger.guava.common.redission;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickStart {

    private static final Logger logger = LoggerFactory.getLogger(QuickStart.class);

    public static void main(String[] args) {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient client = Redisson.create(config);

        RAtomicLong longObject = client.getAtomicLong("o");
        logger.info(longObject.getName());
        longObject.set(401);
        // 同步执行方式
        boolean result = longObject.compareAndSet(401, 402);
        logger.info(String.valueOf(result));
    }
}
