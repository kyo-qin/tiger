package org.tiger.hystrix.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import rx.Observable;
import rx.Observer;

public class HystrixCommon extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(HystrixCommon.class);

    private final String name;

    // 如果不涉及网络访问，建议配置为semaphore而不是线程池
    public HystrixCommon(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("Example1Group"));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        throw new RuntimeException("this command always fails");
        // return "Hello " + name + "!";
    }

    @Override
    protected String getFallback() {
        return "Goodbye " + name + "!";
    }

    public static void main(String[] args) {
        String s = new HystrixCommon("Qintao").execute();
        logger.info(s);
        Observable<String> obs = new HystrixCommon("Bob").observe();
        obs.subscribe((v) -> {
            logger.info("ON NEXT:" + v);
        }, (exception) -> {
            exception.printStackTrace();
        });
        // obs.subscribe(new Observer<String>() {
        //
        // @Override
        // public void onCompleted() {
        //
        // }
        //
        // @Override
        // public void onError(Throwable e) {
        //
        // }
        //
        // @Override
        // public void onNext(String t) {
        // logger.info("ON NEXT:" + t);
        // }
        //
        // });
    }
}
