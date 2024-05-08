package com.defaulttenant.hotswap.datasource.dynamic;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.BeanFactoryDataSourceLookup;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author sys
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> currentDataSource = new ThreadLocal<>();

    public DynamicDataSource(BeanFactory beanFactory) {
        setDataSourceLookup(new BeanFactoryDataSourceLookup(beanFactory));
    }

    @Override
    public String determineCurrentLookupKey() {
        return currentDataSource.get();
    }

    public static void setCurrentDataSource(String dataSource) {
        if (null == dataSource || "".equals(dataSource)) {
            clearCurrentDataSource();
        } else {
            currentDataSource.set(dataSource);
        }
    }

    public static String getCurrentDataSource() {
         return currentDataSource.get();
    }

    public static void clearCurrentDataSource() {
        currentDataSource.remove();
    }

    @Override
    public DataSource determineTargetDataSource() {
        return super.determineTargetDataSource();
    }
}
