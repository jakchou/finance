package com.macro.mall.tiny.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhouzz
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 假设服务器根目录为 /，图片在 /images 目录下
        // 可以按需调整 "file:/images/" 的路径，确保它指向正确的文件夹
       // registry.addResourceHandler("/images/**").addResourceLocations("file:/images/");
    }
}