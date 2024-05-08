package com.defaulttenant.hotswap.timing.job.listeners;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;

public class GlobalJobListener extends JobListenerSupport {

    @Override
    protected Logger getLog() {
      return super.getLog();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
      JobKey jobKey = context.getJobDetail().getKey();
      getLog().info("定时任务 {}-{} 即将执行",jobKey.getGroup(),jobKey.getName());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
      JobKey jobKey = context.getJobDetail().getKey();
      getLog().info("定时任务 {}-{} 被拒绝执行",jobKey.getGroup(),jobKey.getName());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
      JobKey jobKey = context.getJobDetail().getKey();
      getLog().info("定时任务 {}-{} 执行结束，耗时 {}ms",jobKey.getGroup(),jobKey.getName(),context.getJobRunTime());
    }

    @Override
    public String getName() {
      return this.getClass().getSimpleName();
    }
}
