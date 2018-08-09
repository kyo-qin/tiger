package org.tiger.zookeeper.client.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class CreateSessionTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateSessionTest.class);

    public void testCreateSession() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", 5000, 3000, retryPolicy);
        client.start();
        logger.info("Zookeeper session1 established. ");
        // fluent 风格创建
        CuratorFramework client1 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy).namespace("base").build();
        client1.start();
        logger.info("Zookeeper session2 established. ");
    }
}
