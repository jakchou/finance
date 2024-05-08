package com.defaulttenant.hotswap.service.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import com.defaulttenant.hotswap.service.ServiceVersion;

import java.util.List;
import java.util.Objects;

/**
 * 用户服务工厂
 */
@Service
public class UserServiceFactory {

    private Logger logger = LoggerFactory.getLogger(UserServiceFactory.class);
    @Autowired
    List<UserProxyService> userProxyServices;

    /**
     * 获取目标服务
     *
     * @return
     */
    public UserProxyService getTargetService() {
        UserProxyService targetProxyService = userProxyServices.get(0);
        if (userProxyServices.size() == 1) {
            return targetProxyService;
        }
        int targetVersion = Integer.MIN_VALUE;
        //选取@ServiceVersion最新的服务
        for (int i = 0; i < userProxyServices.size(); i++) {
            UserProxyService proxyService = userProxyServices.get(i);
            ServiceVersion serviceVersion = AnnotationUtils.findAnnotation(proxyService.getClass(), ServiceVersion.class);
            if (Objects.nonNull(serviceVersion) && serviceVersion.value() > targetVersion) {
                targetVersion = serviceVersion.value();
                targetProxyService = proxyService;
            }
        }
        logger.info("userService route targetService = {}", targetProxyService.getClass().getSimpleName());
        return targetProxyService;
    }

}
