package com.defaulttenant.hotswap.timing.config;

import com.defaulttenant.hotswap.timing.datasource.ConnectionProxy;
import com.defaulttenant.hotswap.exception.InnerException;
import com.defaulttenant.hotswap.util.JacksonUtils;
import com.defaulttenant.hotswap.util.SpringUtils;
import com.defaulttenant.hotswap.config.LcpProperties;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.SchedulerConfigException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import liquibase.integration.spring.SpringLiquibase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(value = "spring.quartz.enabled", havingValue = "true")
public class SchedulerConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfiguration.class);
    private static final String SQL_DELETE_FROM = "DELETE FROM ";
    private static LcpProperties lcpProperties;

    /** make sure liquibase load before quartz **/
    @Autowired(required = false)
    private SpringLiquibase springLiquibase;

    @Bean
    public SchedulerFactoryBean quartzScheduler(ObjectProvider<SchedulerFactoryBeanCustomizer> customizers,
        ObjectProvider<JobDetail> jobDetails, Map<String, Calendar> calendars,
            ObjectProvider<Trigger> triggers, ApplicationContext applicationContext,
                LcpProperties lcpProperties, CustomQuartzProperties properties) {
        LOGGER.info("config SchedulerFactoryBean");
        setProperties(lcpProperties);
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);
        if (properties.getSchedulerName() != null) {
            schedulerFactoryBean.setSchedulerName(properties.getSchedulerName());
        }
        properties.getProperties().put(StdSchedulerFactory.PROP_JOB_STORE_CLASS, MyJobStoreTX.class.getName());
        schedulerFactoryBean.setAutoStartup(properties.isAutoStartup());
        schedulerFactoryBean.setStartupDelay((int) properties.getStartupDelay().getSeconds());
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(properties.isWaitForJobsToCompleteOnShutdown());
        schedulerFactoryBean.setOverwriteExistingJobs(properties.isOverwriteExistingJobs());
        if (!properties.getProperties().isEmpty()) {
            schedulerFactoryBean.setQuartzProperties(asProperties(properties.getProperties()));
        }
        schedulerFactoryBean.setJobDetails(jobDetails.orderedStream().toArray(JobDetail[]::new));
        schedulerFactoryBean.setCalendars(calendars);
        schedulerFactoryBean.setTriggers(triggers.orderedStream().toArray(Trigger[]::new));
        customizers.orderedStream().forEach((customizer) -> customizer.customize(schedulerFactoryBean));
        // 不能让customizers设置dataSource否则org.quartz.jobStore.class这个配置项会无效，导致不执行MyJobStoreTX
        schedulerFactoryBean.setDataSource(null);
        return schedulerFactoryBean;
    }

    public static class MyJobStoreTX extends JobStoreTX {
        private static final String[] CLEAN_TABLE_NAMES = new String[] {Constants.TABLE_SCHEDULER_STATE,
        Constants.TABLE_LOCKS, Constants.TABLE_FIRED_TRIGGERS, Constants.TABLE_SIMPLE_TRIGGERS,
        Constants.TABLE_CRON_TRIGGERS, Constants.TABLE_BLOB_TRIGGERS, Constants.TABLE_TRIGGERS,
        Constants.TABLE_JOB_DETAILS, Constants.TABLE_CALENDARS};

        @Override
        public void initialize(ClassLoadHelper classLoadHelper, SchedulerSignaler schedSignaler) throws SchedulerConfigException {
            resetData();
            super.initialize(classLoadHelper, schedSignaler);
        }


        @Override
        protected Connection getConnection() throws JobPersistenceException {
            return new ConnectionProxy(super.getConnection(), lcpProperties.getQuartzTables());
        }

        private void resetData() {
            try (Connection connection = super.getConnection();
            Statement statement = connection.createStatement()) {
                LOGGER.info("start to reset quartz init data...");
                connection.setAutoCommit(false);
                // 下面顺序不能变，因为可能存在外键
                Map<String, String> tableMap = ( null == lcpProperties.getQuartzTables() ?
                Collections.EMPTY_MAP : lcpProperties.getQuartzTables() );
                List<String> renameTables = new ArrayList<>(CLEAN_TABLE_NAMES.length);
                for (String tableName : CLEAN_TABLE_NAMES) {
                    String renameTable = tableMap.getOrDefault(tableName, tableName);
                    statement.addBatch(SQL_DELETE_FROM + renameTable);
                    renameTables.add(renameTable);
                }
                int[] result = statement.executeBatch();
                connection.commit();
                LOGGER.info("reset quartz init data done! affect rows {}: {}",
                JacksonUtils.toJson(renameTables), JacksonUtils.toJson(result));
            } catch (SQLException | JobPersistenceException e) {
                throw new InnerException("reset quartz init data error!", e);
            }
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    private synchronized static void setProperties(LcpProperties lcpProperties) {
        SchedulerConfiguration.lcpProperties = lcpProperties;
    }
}

