package org.tiger.guava.common;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class Cache {

    public static void main(String[] args) {

        LoadingCache<String, String> caches = CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener(new RemovalListener<String, String>() {

                    @Override
                    public void onRemoval(RemovalNotification<String, String> notification) {

                    }

                }).build(new CacheLoader<String, String>() {
                    public String load(String key) {
                        // 1. 从redis加载
                        // 2. 从数据库加载
                        // 3. 存入redis
                        return "Hello: " + key;
                    }
                });
        try {
            System.out.println(caches.get("qintao"));
            caches.put("qintao", "right");
            System.out.println(caches.get("qintao"));
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        caches.put("k1", "v1");
        caches.put("k2", "v2");
        caches.put("k3", "v3");
        caches.put("k4", "v4");
        caches.put("k1", "v1");
        caches.put("k5", "v5");
        caches.put("k6", "v6");
        caches.put("k7", "v7");
        caches.put("k1", "v1");
        caches.put("k8", "v8");
        caches.put("k9", "v9");
        System.out.println(caches.asMap());

    }
}
