package org.tiger.storm.common;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import junit.framework.TestCase;

public class GuavaCacheTest extends TestCase {

    public void testApp() {

        CacheLoader<String, String> loader = new CacheLoader<String, String>() {
            public String load(String key) throws Exception {
                Thread.sleep(1000);
                if ("key".equals(key))
                    return null;
                System.out.println(key + " is loaded from a cacheLoader!");
                return key + "'s value";
            }
        };

        RemovalListener<String, String> removalListener = new RemovalListener<String, String>() {
            public void onRemoval(RemovalNotification<String, String> removal) {
                System.out.println("[" + removal.getKey() + ":" + removal.getValue() + "] is evicted!");
            }
        };

        LoadingCache<String, String> testCache = CacheBuilder.newBuilder().maximumSize(7).expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener(removalListener).recordStats().build(loader);

        for (int i = 0; i < 10; i++) {
            String key = "key" + i;
            String value = "value" + i;
            testCache.put(key, value);
            System.out.println("[" + key + ":" + value + "] is put into cache!");
        }

    }

}
