package com.macro.mall.tiny.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouzz
 */
@Configuration
public class ThreadConfig {

    @Bean
    public ThreadPoolExecutor orderThread() {
        return new ThreadPoolExecutor(1, 1, 1, TimeUnit.HOURS, new ArrayBlockingQueue<>(1),new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"orderThread");
        }
    },new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
