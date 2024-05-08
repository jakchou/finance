package com.defaulttenant.hotswap.web.interceptor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sys
 */

public class RateLimiterSimpleWindow {

    /** allow two requests for downloading files in one second **/
    private static Integer QPS = 2;

    private static long TIME_WINDOWS = 1000;

    private static AtomicInteger REQ_COUNT = new AtomicInteger();

    private static long START_TIME = System.currentTimeMillis();

    private RateLimiterSimpleWindow() {
    }

    public synchronized static boolean tryAcquire() {
        if ((System.currentTimeMillis() - START_TIME) > TIME_WINDOWS) {
            REQ_COUNT.set(0);
            START_TIME = System.currentTimeMillis();
        }
        return REQ_COUNT.incrementAndGet() <= QPS;
    }

}
