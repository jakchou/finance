package com.defaulttenant.hotswap.timing.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Component
public class DataSourcePostProcessor implements BeanPostProcessor {
    @Autowired
    private Environment environment;
    /** 已经代理的数据源对象就不需要再代理了，这种情况在接入类似apollo等配置中心进行动态配置的情况可能出现 **/
    private Set<String> proxiedBeanNames = new HashSet<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource && ! (bean instanceof XADataSource) && !proxiedBeanNames.contains(beanName)) {
            Enhancer enhancer = new Enhancer();
            Class clazz = bean.getClass();
            if (AopUtils.isAopProxy(bean)) {
                clazz = AopProxyUtils.ultimateTargetClass(bean);
            }
            if (Modifier.isFinal(clazz.getModifiers())) {
                enhancer.setSuperclass(DataSource.class);
            } else {
                enhancer.setSuperclass(clazz);
            }
            DataSourceMethodInterceptor dataSourceMethodInterceptor = new DataSourceMethodInterceptor((DataSource)bean);
            enhancer.setCallback(dataSourceMethodInterceptor);
            Object proxyObj = enhancer.create();
            setNeverRebind(bean, proxyObj);
            proxiedBeanNames.add(beanName);
            return proxyObj;
        }
        return bean;
    }

    /**
    * dataSource实例创建后一般不进行rebind的，这在接入运行配置中心进行动态配置的时候会有问题
    * 先不考虑可以在运行时修改数据源配置的情况(实际上这种情况也几乎不会有)
    * @See org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder **/
    private void setNeverRebind(Object originObj, Object proxyObj) {
        String delimiter = ",";
        String oldValue = environment.getProperty("spring.cloud.refresh.never-refreshable",
        "com.zaxxer.hikari.HikariDataSource");
        List<String> oldValues = Arrays.asList(StringUtils.split(oldValue, delimiter));
        if (oldValues.contains(originObj.getClass().getName())) {
            String newValue = String.join(delimiter, oldValue, proxyObj.getClass().getName());
            System.setProperty("spring.cloud.refresh.never-refreshable", newValue);
        }
    }
}
