package com.defaulttenant.hotswap.datasource.dynamic;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Order(0) In order to be executed ahead of other AOP interceptors, such as @Transactional,
 * these annotations may invoke the data source ahead of time resulting in a dynamic switching implementation
 * @author sys
 */
@Aspect
@Order(0)
@Component
public class DynamicDataSourceAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    @Pointcut("@annotation(com.defaulttenant.hotswap.datasource.dynamic.DataSource) || " +
            "within(@com.defaulttenant.hotswap.datasource.dynamic.DataSource *)")
    public void pointCut() {
        LOGGER.debug("DynamicDataSourceAspect pointCut");
    }

    @Around("pointCut()")
    public Object arround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (null == dataSource) {
            // for mybatis mapper
            dataSource = method.getDeclaringClass().getAnnotation(DataSource.class);
        }

        String prevDataSource = DynamicDataSource.getCurrentDataSource();
        if (null != dataSource) {
            DynamicDataSource.setCurrentDataSource(dataSource.value());
            LOGGER.debug("use datasource {}", dataSource.value());
        }

        try {
            return joinPoint.proceed();
        } finally {
            DynamicDataSource.setCurrentDataSource(prevDataSource);
        }
    }
}
