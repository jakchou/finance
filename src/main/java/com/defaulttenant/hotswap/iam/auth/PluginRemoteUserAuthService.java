package com.defaulttenant.hotswap.iam.auth;

import com.netease.cloud.nuims.auth.api.bean.AuthInfo;
import com.netease.cloud.nuims.plugin.starter.PluginConfigProperties;
import com.defaulttenant.hotswap.context.UserContext;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 远程用户中心插件认证服务
 *
 * @author system
 */
@Service
public class PluginRemoteUserAuthService implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginRemoteUserAuthService.class);
    /**
     * 默认Normal类型
     */
    private static final String NORMAL_LOGIN_TYPE = "Normal";
    @Resource
    private PluginConfigProperties pluginConfigProperties;
    /**
     * 插件远程用户中心配置标识
     */
    private static final String IF_REMOTE_USER_CENTER = "ifRemoteUserCenter";
    @Resource
    private com.netease.cloud.nuims.auth.domain.authen.AuthService pluginAuthService;
    private String authType = NORMAL_LOGIN_TYPE;

    @Override
    public Map<String, String> getSession(HttpServletRequest request) {
        Map<String,String> sessionMap = new HashMap<>();
        try {
            AuthInfo authInfo = pluginAuthService.auth(request, authType, null);
            UserContext.setIfRemoteUserCenter(true);
            if (AuthInfo.RESULT_SUCCESS.equals(authInfo.getResult())) {
                UserContext.UserInfo userInfo = new UserContext.UserInfo();
                userInfo.setUserId(authInfo.getUserId());
                userInfo.setUserName(authInfo.getUserName());
                userInfo.setNickName(authInfo.getDisplayName());
                userInfo.setSource(authType);
                if (Objects.isNull(authInfo.getUserId())) {
                    UserContext.setCurrentUserInfo(null);
                } else {
                    UserContext.setCurrentUserInfo(userInfo);
                }
            }
        } catch (com.netease.cloud.nuims.auth.api.exception.AuthException e) {
            LOGGER.error("Error: {}", e.getMessage());
        }
        return sessionMap;
    }

    @Override
    public boolean match(HttpServletRequest request) {
        Map<String, PluginConfigProperties.Plugin> unionAuthTypes = this.pluginConfigProperties.getList();
        Iterator<String> pluginIterator = unionAuthTypes.keySet().iterator();
        while (pluginIterator.hasNext()) {
            String pluginName = pluginIterator.next();
            authType = NORMAL_LOGIN_TYPE.equals(pluginName) ? authType : pluginName;
        }
        if (!NORMAL_LOGIN_TYPE.equals(authType) && Objects.nonNull(unionAuthTypes.get(authType).getConfig()) && unionAuthTypes.get(authType).getConfig().containsKey(IF_REMOTE_USER_CENTER)) {
            return true;
        }
        return false;
    }

    @Override
    public void clearSession(HttpServletRequest request, HttpServletResponse response) {
        try {
            pluginAuthService.logout(request, authType, null);
        } catch (com.netease.cloud.nuims.auth.api.exception.AuthException e) {
           LOGGER.error("Error: {}", e.getMessage());
        }
    }

    @Override
    public String type() {
        return AuthManager.TYPE_PLUGIN + "REMOTE";
    }
}
