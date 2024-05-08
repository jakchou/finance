package com.defaulttenant.hotswap.iam.auth;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 认证领域管理类
 * @author sys
 */
@Component
public class AuthManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthManager.class);
    private static final String LIBRARY_AUTH_SPI_CLASS = "com.netease.lowcode.auth.api.auth.LCAPAuthService";
    Map<String, AuthService> authServiceMap = new LinkedHashMap<>();

    /**
     * 插件类型
     */
    public static final String TYPE_PLUGIN = "PLUGIN_";

    /**
     * 系统类型
     */
    public static final String TYPE_DEFAULT = "DEFAULT_";

    /**
     * 依赖库类型
     */
    public static final String TYPE_LIBRARY = "LIBRARY_";

    /**
    * NASL扩展点
    */
    public static final String TYPE_NASL = "NASL_";

    /**
     * 内置的认证服务，包括
     * DefaultAuthService
     * PluginRemoteUserAuthService (用户下沉才会加载)
     */
    @Resource
    private List<AuthService> buildInAuthServiceList;


    @PostConstruct
    public void init() {
        loadLibraryAuthServiceFromSPI();
        loadBuildInAuthService();
    }


    /**
     * 加载内置方式的方式认证服务
     * 包含：
     * 1.系统默认认证服务
     * 2.采用了远程用户中心的插件服务
     */
    private void loadBuildInAuthService() {
        for (AuthService authService : buildInAuthServiceList) {
            authServiceMap.put(authService.type(), authService);
        }
    }


    /**
     * 加载SPI方式的依赖库方式认证服务
     */
    private void loadLibraryAuthServiceFromSPI() {
        try {
            Class<?> authClass = Class.forName(LIBRARY_AUTH_SPI_CLASS);
            ServiceLoader.load(authClass).forEach(service -> {
                ExtensionLibraryAuthService extensionLibraryAuthService = new ExtensionLibraryAuthService(service);
                if(authServiceMap.containsKey(TYPE_LIBRARY+extensionLibraryAuthService.type())){
                     LOGGER.error("[{}]SPI依赖库重复加载,ClassName={}", extensionLibraryAuthService.type(),extensionLibraryAuthService.getClass().getName());
                }
                authServiceMap.put(TYPE_LIBRARY + extensionLibraryAuthService.type(), extensionLibraryAuthService);
                LOGGER.info("自定义认证[{}]SPI依赖库认证服务加载成功", extensionLibraryAuthService.type());
            });
        } catch (ClassNotFoundException e) {
            LOGGER.info("没有自定义认证服务spi实现");
        }
    }


    /**
     * 根据请求标识查找认证服务
     *
     * @param request
     * @return
     */
    public List<AuthService> getAuthService(HttpServletRequest request) {
        List<AuthService> matchedAuthServiceList = new ArrayList<>();
        if (MapUtils.isNotEmpty(authServiceMap)) {
            Iterator<String> keys = authServiceMap.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    AuthService authService = authServiceMap.get(key);
                    if (authService.match(request)) {
                        if(authService.type().startsWith(TYPE_NASL)){
                            //如果有系统扩展逻辑实现，则优先取用
                            matchedAuthServiceList.add(0,authService);
                        }else {
                            matchedAuthServiceList.add(authService);
                        }
                    }
                } catch (Exception ignoreException) {
                    LOGGER.error("[{}]类型AuthService，match异常", key);
                }
            }
        }
        return matchedAuthServiceList;
    }

}
