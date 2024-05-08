package com.defaulttenant.hotswap.service.system.configuration;

import static com.defaulttenant.hotswap.config.Constants.RESERVED_PROPERTY_KEY_PREFIX;
import static com.defaulttenant.hotswap.config.Constants.USER_APP_PROPERTY_KEY_PREFIX;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class NaslConfigurationComponent {

    @Autowired
    private Environment environment;

    @Value("${lcp.private}")
    private String privateKeys;

    /**
     * 获取用户自定义配置
     * @param key
     * @return
     */
    public String getCustomConfig(String key) {
        String targetKey = USER_APP_PROPERTY_KEY_PREFIX.concat(key);
        return this.environment.getProperty(targetKey);
    }

    /**
     * 获取用户自定义配置
     *
     * @param group 配置所属组
     * @param key   配置名
     * @return 自定义配置
     */
    public String getCustomConfig(String group, String key) {
        if (StringUtils.isBlank(group)) {
            return environment.getProperty(USER_APP_PROPERTY_KEY_PREFIX.concat(key));
        }
        if (!StringUtils.startsWithAny(group, RESERVED_PROPERTY_KEY_PREFIX.toArray(new String[0]))) {
            return null;
        }

        String targetKey = StringUtils.joinWith(".", group, key);
        return environment.getProperty(targetKey);
    }

    /**
     * 给前端调用的接口，获取用户自定义配置
     * @param group
     * @param key
     * @return
     */
    public String getUnPrivateCustomConfig(String group, String key) {
        if (StringUtils.isBlank(group)) {
            return environment.getProperty(USER_APP_PROPERTY_KEY_PREFIX.concat(key));
        }

        if (!StringUtils.startsWithAny(group, RESERVED_PROPERTY_KEY_PREFIX.toArray(new String[0]))) {
            return null;
        }
        String targetKey = StringUtils.joinWith(".", group, key);
        if (StringUtils.isNotBlank(privateKeys) && privateKeys.contains(targetKey)) {
            return null;
        }
        return environment.getProperty(targetKey);
    }
}
