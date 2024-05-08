package com.defaulttenant.hotswap.timing; 

import org.quartz.DisallowConcurrentExecution; 
import com.defaulttenant.hotswap.timing.job.CronJob; 
import org.springframework.stereotype.Component; 
import org.quartz.JobExecutionException; 
import org.quartz.JobExecutionContext; 
import org.springframework.beans.factory.annotation.Autowired; 
import com.defaulttenant.hotswap.service.logics.Logic62CustomizeService; 

@Component
@DisallowConcurrentExecution
public class Logic62CustomizeJob extends CronJob{

    @Autowired
    private Logic62CustomizeService logic62CustomizeService;
    public Logic62CustomizeJob() {
        super("*/5 * * * * ?", "hotswap", "logic62");
    } 

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            logic62CustomizeService.logic62();
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
