package com.defaulttenant.hotswap.timing.job;

public abstract class CronJob extends LogicJob {
    protected String cronExpression;

    protected CronJob(String cronExpression, String appName, String logicName) {
        super(appName, logicName);
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
