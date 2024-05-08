package com.defaulttenant.hotswap.timing; 

import org.quartz.DisallowConcurrentExecution; 
import com.defaulttenant.hotswap.timing.job.CronJob; 
import org.springframework.stereotype.Component; 
import org.quartz.JobExecutionException; 
import org.quartz.JobExecutionContext; 
import com.defaulttenant.hotswap.service.logics.Logic61CustomizeService; 
import org.springframework.beans.factory.annotation.Autowired; 

@Component
@DisallowConcurrentExecution
public class Logic61CustomizeJob extends CronJob{

    @Autowired
    private Logic61CustomizeService logic61CustomizeService;
    public Logic61CustomizeJob() {
        super("*/5 * * * * ?", "hotswap", "logic61");
    } 

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            logic61CustomizeService.logic61();
        } catch (Exception e) {
            throw new JobExecutionException(e.getMessage(), e.getCause()); 
        } 
    } 

    @Override
    public String getLogicName() {
        return this.logicName;
    } 

    @Override
    public String getAppName() {
        return this.appName;
    } 


}
