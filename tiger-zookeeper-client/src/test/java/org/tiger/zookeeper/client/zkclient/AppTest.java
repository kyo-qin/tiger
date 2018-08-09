package org.tiger.zookeeper.client.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
//    public void testCreateZKSession() {
//        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
//        logger.info("ZooKeeper session established.");
//    }

    public void testCreateZKNode() {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        String path = "/zk-book/c1";
        zkClient.createPersistent(path, true);
        logger.info("success create znode.");
        zkClient.deleteRecursive(path);
        logger.info("delete path");
    }
    
}
