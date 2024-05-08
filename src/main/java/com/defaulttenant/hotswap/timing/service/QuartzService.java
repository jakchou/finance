package com.defaulttenant.hotswap.timing.service;

import com.defaulttenant.hotswap.timing.job.CronJob;
import com.defaulttenant.hotswap.timing.job.LogicJob;
import com.defaulttenant.hotswap.timing.job.listeners.GlobalJobListener;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@ConditionalOnProperty(value = "spring.quartz.enabled", havingValue = "true")
public class QuartzService implements ApplicationRunner, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(QuartzService.class);
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, LogicJob> logicJobsBeans = applicationContext.getBeansOfType(LogicJob.class);
        try {
          scheduler.start();
          scheduler.getListenerManager().addJobListener(new GlobalJobListener());
          for (LogicJob job : logicJobsBeans.values()) {
            CronJob cronJob = (CronJob) job;
            this.addJob(job.getClass(), job.getLogicName(), job.getAppName(), cronJob.getCronExpression(), null);
          }
        } catch (Exception e) {
          logger.error("定时任务调度异常", e);
        }
  }
    /**
    * 添加一个任务
    * @param jobClass
    * @param jobGroup
    * @param jobName
    * @param jobTime
    * @param jobMap
    * @throws SchedulerException
    */
    public void addJob(Class<? extends LogicJob> jobClass, String jobGroup, String jobName, String jobTime, Map jobMap) throws SchedulerException {
        JobDetail build = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).requestRecovery(false).build();
        if (jobMap != null && jobMap.size() > 0) {
            build.getJobDataMap().putAll(jobMap);
        }
        if (scheduler.checkExists(build.getKey())) {
            scheduler.deleteJob(build.getKey());
        }
        Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName+System.currentTimeMillis(), jobGroup)
        .withSchedule(CronScheduleBuilder.cronSchedule(jobTime).withMisfireHandlingInstructionDoNothing()).build();
        scheduler.scheduleJob(build, cronTrigger);
    }
    @Override
    public void destroy() throws Exception {
        List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
        for (String groupName : triggerGroupNames) {
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(groupName);
            Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(groupMatcher);
            for (TriggerKey triggerKey : triggerKeySet) {
                if (scheduler.getTriggerState(triggerKey) == Trigger.TriggerState.ERROR) {
                    scheduler.resetTriggerFromErrorState(triggerKey);
                }
            }
        }
    }
    /**
    * 更新任务时间表达式
    * @param jobGroup
    * @param jobName
    * @param jobTime
    * @throws SchedulerException
    */
    public void updateJob(String jobGroup, String jobName, String jobTime) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
        .withSchedule(CronScheduleBuilder.cronSchedule(jobTime)).build();
        scheduler.rescheduleJob(triggerKey,trigger);
    }

    /**
    * 删除任务
    * @param jobGroup
    * @param jobName
    * @throws SchedulerException
    */
    public void deleteJob(String jobGroup,String jobName) throws SchedulerException {
        scheduler.deleteJob(new JobKey(jobName,jobGroup));
    }

    public void pauseJob(String jobGroup,String jobName) throws SchedulerException {
        scheduler.pauseJob(new JobKey(jobName,jobGroup));
    }

    /**
    * 恢复任务
    * @param jobGroup
    * @param jobName
    * @throws SchedulerException
    */
    public void resumeJob(String jobGroup,String jobName) throws SchedulerException {
        scheduler.resumeJob(new JobKey(jobName,jobGroup));
    }

    /**
     * 立刻执行一个任务
     * @param jobGroup
     * @param jobName
     * @throws SchedulerException
     */
    public void runAJobNow(String jobGroup,String jobName) throws SchedulerException {
        scheduler.triggerJob(new JobKey(jobName,jobGroup));
    }
}
