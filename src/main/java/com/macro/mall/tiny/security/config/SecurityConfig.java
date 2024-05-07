package com.macro.mall.tiny.security.config;

import com.macro.mall.tiny.security.component.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.CollectionUtils;


/**
 * SpringSecurity 6.x以上新用法配置
 * 为避免循环依赖，仅用于配置HttpSecurity
 * Created by macro on 2019/11/5.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private DynamicAuthorizationManager dynamicAuthorizationManager;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

            httpSecurity
                    .authorizeHttpRequests((auth) -> {
                        auth.requestMatchers(ignoreUrlsConfig.getUrls().toArray(new String[ignoreUrlsConfig.getUrls().size()])).permitAll()
                                .anyRequest().access(dynamicAuthorizationManager);
                    })
                    .csrf((csrf)->{
                        try {
                            csrf.disable().sessionManagement((sessionManagement)->{
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).exceptionHandling((exceptionHandling)->{
                        exceptionHandling.accessDeniedHandler(restfulAccessDeniedHandler)
                                .authenticationEntryPoint(restAuthenticationEntryPoint);
                    }).httpBasic((httpBasic)->{
                        httpBasic.disable();
                    }).addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
