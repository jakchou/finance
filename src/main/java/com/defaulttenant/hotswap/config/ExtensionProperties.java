package com.defaulttenant.hotswap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * 扩展配置项，用户自定义配置项
 *
 * @author sys
 * @date 2022-06-09 16:14
 */
@Configuration
@ConfigurationProperties(prefix = "custom")
public class ExtensionProperties extends HashMap<String, String> {
}
