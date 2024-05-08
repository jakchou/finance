package com.defaulttenant.hotswap.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T getBean(String beanName, Class<T> glass) {
        return applicationContext.getBean(beanName, glass);
    }

    public static <T> T getBean(Class<T> glass) {
        return applicationContext.getBean(glass);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtils.applicationContext = applicationContext;
    }
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

}
