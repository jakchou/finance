package com.defaulttenant.hotswap.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.defaulttenant.hotswap.web.interceptor.AccessLogFilter;
import com.defaulttenant.hotswap.web.interceptor.BasePathFilter;
import com.defaulttenant.hotswap.web.interceptor.GlobalContextFilter;
import com.defaulttenant.hotswap.web.interceptor.TraceIdFilter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfiguration implements ServletContextInitializer {
    private final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    private final Environment env;

    public WebConfiguration(Environment env) {
        this.env = env;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (env.getActiveProfiles().length != 0) {
            log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }

        log.info("Web application fully configured");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/management/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
            source.registerCorsConfiguration("/v3/api-docs", config);
            source.registerCorsConfiguration("/swagger-resources", config);
        }
        return new CorsFilter(source);
    }

    @Bean
    public GlobalContextFilter globalContextFilter() {
        return new GlobalContextFilter();
    }

    @Bean
    public FilterRegistrationBean traceIdFilterReg() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TraceIdFilter());
        registration.addUrlPatterns("/api/*");
        registration.setName(TraceIdFilter.class.getSimpleName());
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean accessLogFilterReg(LcpProperties lcpProperties) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AccessLogFilter(lcpProperties));
        registration.addUrlPatterns("/api/*");
        registration.setName(AccessLogFilter.class.getSimpleName());
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public DataSourceHealthIndicator databaseHealthIndicator(DataSource dataSource) {
        return new DataSourceHealthIndicator(dataSource);
    }

    @Bean
    public BasePathFilter basePathFilter() {
        return new BasePathFilter();
    }
}
