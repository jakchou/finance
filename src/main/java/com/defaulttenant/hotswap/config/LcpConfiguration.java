package com.defaulttenant.hotswap.config;

import com.defaulttenant.hotswap.service.system.UserServiceFactory;
import com.defaulttenant.hotswap.web.interceptor.AuthFilter;
import com.netease.cloud.nuims.auth.domain.authen.AuthService;
import com.netease.cloud.nuims.plugin.starter.PluginConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@Configuration
@EnableScheduling
public class LcpConfiguration {
    @Value("${lcp.authCenter.address}")
    private String authAddress;

    @Value("${lcp.authCenter.host}")
    private String authHost;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${lcp.audit.address}")
    private String auditServer;

    @Value("${custom.upload_apiAuthFlag:false}")
    private Boolean uploadApiAuthFlag;

    @Autowired
    private RemoteUserCenterProperties remoteUserCenterConfig;

    @Resource
    private LcpProperties lcpProperties;

    @Resource
    private MessageSource messageSource;

    @Resource
    private AuthService authService;

    @Resource
    private PluginConfigProperties pluginConfigProperties;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnProperty(value = "lcp.authCenter.enable", havingValue = "true")
    public FilterRegistrationBean authFilter(UserServiceFactory userServiceFactory) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthFilter(lcpProperties.getNoAuthPaths(), messageSource, userServiceFactory, authService, pluginConfigProperties, uploadApiAuthFlag));
        registration.addUrlPatterns("/api/*", "/upload/*");
        registration.setName(AuthFilter.class.getSimpleName());
        registration.setOrder(1);
        return registration;
    }
}