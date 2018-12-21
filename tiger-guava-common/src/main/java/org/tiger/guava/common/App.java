package org.tiger.guava.common;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(100, 20, TimeUnit.MILLISECONDS);
        //RateLimiter rateLimiter = RateLimiter.create(100);
        double r = rateLimiter.acquire(110);
        System.out.println(r);
        r = rateLimiter.acquire(120);
        System.out.println(r);
        r = rateLimiter.acquire(120);
        System.out.println(r);
        r = rateLimiter.acquire(120);
        System.out.println(r);
        r = rateLimiter.acquire(120);
        System.out.println(r);
        r = rateLimiter.acquire(120);
        System.out.println(r);
        r = rateLimiter.acquire(120);
        System.out.println(r);
    }
}
