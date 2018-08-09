package org.tiger.zookeeper.client.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class ZKGetDataTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(ZKGetDataTest.class);

    public void testGetData() throws InterruptedException {
        String path = "/zk-book";
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        if (!zkClient.exists(path)) {
            zkClient.createEphemeral(path, "created by qintao");
        } else {
            zkClient.writeData(path, "changed by qintao");
        }

        zkClient.subscribeDataChanges(path, new IZkDataListener() {

            public void handleDataDeleted(String dataPath) throws Exception {
                logger.info("Node " + dataPath + " deleted.");
            }

            public void handleDataChange(String dataPath, Object data) throws Exception {
                logger.info("Node " + dataPath + " changed, new data: " + data);
            }
        });

        logger.info(zkClient.readData(path).toString());
        zkClient.writeData(path, "changed by qintao twice");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(10000);
    }
}
