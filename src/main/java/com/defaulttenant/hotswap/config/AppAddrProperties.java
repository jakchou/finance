package com.defaulttenant.hotswap.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "gw.app")
public class AppAddrProperties {
    private Map<String, String> addr = new HashMap<>();

    public Map<String, String> getAddr() {
        return addr;
    }
}
