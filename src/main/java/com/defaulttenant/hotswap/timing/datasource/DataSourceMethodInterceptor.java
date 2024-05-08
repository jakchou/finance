package com.defaulttenant.hotswap.timing.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Objects;

/**
* use cglib to proxy datasource
* @author sys
*/
public class DataSourceMethodInterceptor implements MethodInterceptor {

    private DataSource dataSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceMethodInterceptor.class);

    public DataSourceMethodInterceptor(DataSource dataSource) {
        Objects.requireNonNull(dataSource, "datasource required not null");
        this.dataSource = dataSource;
        LOGGER.debug("use cglib to proxy dataSource");
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        // getConnection()
        boolean isGetConnection = "getConnection".equals(method.getName()) && method.getParameterCount() == 0;
        // getConnection(String username, String password)
        boolean isGetConnectionWithParam = "getConnection".equals(method.getName()) &&
        method.getParameterCount() == 2 &&
        method.getParameterTypes()[0].equals(String.class) &&
        method.getParameterTypes()[1].equals(String.class);
        if (isGetConnection) {
            return dataSource.getConnection();
        } else if (isGetConnectionWithParam) {
            return dataSource.getConnection((String) objects[0], (String) objects[1]);
        } else {
            return methodProxy.invoke(dataSource, objects);
        }
    }
}
