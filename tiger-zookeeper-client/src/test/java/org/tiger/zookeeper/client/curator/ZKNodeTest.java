package org.tiger.zookeeper.client.curator;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class ZKNodeTest extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(ZKNodeTest.class);

    public void testZKNode() throws Exception {
        // CountDownLatch semaphore = new CountDownLatch(2);
        String path = "/zk-book/c1";
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
        // 节点监听
        // Path
        // Cache：监视一个路径下1）孩子结点的创建、2）删除，3）以及结点数据的更新。产生的事件会传递给注册的PathChildrenCacheListener。
        // Node Cache：监视一个结点的创建、更新、删除，并将结点的数据缓存在本地。
        // Tree Cache：Path Cache和Node
        // Cache的“合体”，监视路径下的创建、更新、删除事件，并缓存路径下所有孩子结点的数据。
        final NodeCache cache = new NodeCache(client, path, false);
        cache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                logger.info("Node data update, new data: " + new String(cache.getCurrentData().getData()));
            }
        });
        cache.start(true);
        logger.info("success create znode: " + path);
        // 获取数据
        logger.info("get data: " + new String(client.getData().forPath(path), "utf-8"));
        // 更新数据
        client.setData().forPath(path, "新内容".getBytes());
        logger.info("get data: " + new String(client.getData().forPath(path), "utf-8"));
        Thread.sleep(3000);
        cache.close();
    }
}
