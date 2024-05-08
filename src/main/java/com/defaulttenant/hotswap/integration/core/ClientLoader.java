package com.defaulttenant.hotswap.integration.core;

import com.netease.cloud.RemoteCallApi;
import com.netease.cloud.RemoteCallApiClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class ClientLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, Object> targetClients = applicationContext.getBeansWithAnnotation(RemoteCallApiClient.class);
        if(CollectionUtils.isEmpty(targetClients)) {
            return;
        }
        for(Object bean: targetClients.values()) {
            if(!(bean instanceof RemoteCallApi)) {
                continue;
            }
            RemoteCallApi remoteCallApi = (RemoteCallApi) bean;
            ClientContext.register(remoteCallApi.protocolType(), remoteCallApi);
        }
    }
}
