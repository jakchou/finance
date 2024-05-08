package com.defaulttenant.hotswap.timing.job;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LogicJob implements Job {
    protected Logger logger;
    protected String appName;
    protected String logicName;
    protected String triggerType;
    protected LogicJob(String appName ,String logicName) {
        this(appName,logicName,TriggerType.CRONTRIGGER.name());
        logger = LoggerFactory.getLogger(this.getClass());
    }
    protected LogicJob(String appName ,String logicName ,String triggerType){
        this.appName = appName;
        this.logicName = logicName;
        this.triggerType = triggerType;
        logger = LoggerFactory.getLogger(this.getClass());
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setLogicName(String logicName) {
        this.logicName = logicName;
    }

    public String getAppName() {
        return appName;
    }

    public String getLogicName() {
        return logicName;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
}
