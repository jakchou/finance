package com.defaulttenant.hotswap.iam.auth;

import com.defaulttenant.hotswap.context.UserContext;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

/**
 * @author system
 */
public class ExtensionLibraryAuthService implements AuthService {

    private static final String SESSION_USERID_STR = "UserId";
    private static final String SESSION_USERNAME_STR = "UserName";
    private static final String SESSION_PHONE_STR = "Phone";
    private static final String SESSION_EMAIL_STR = "Email";
    private static final String SESSION_NICKNAME_STR = "NickName";
    private static final String SESSION_SOURCE_STR = "Source";
    /**
    * 可为空，预留扩展使用
    */
    private static final String SESSION_EXTRA_STR = "ExtraInfo";

    private Object authServiceInstance;
    private Method typeMethod;
    private Method getSessionMethod;
    private Method matchMethod;
    private Method clearSessionMethod;
    private Method isRemoteUserMethod;
    private Method propertiesMethod;

    public ExtensionLibraryAuthService(Object authServiceInstance) {
        this.authServiceInstance = authServiceInstance;
        this.typeMethod = ReflectionUtils.findMethod(authServiceInstance.getClass(), "type");
        this.getSessionMethod = ReflectionUtils.findMethod(authServiceInstance.getClass(), "getSession", HttpServletRequest.class);
        this.matchMethod = ReflectionUtils.findMethod(authServiceInstance.getClass(), "match", HttpServletRequest.class);
        this.isRemoteUserMethod = ReflectionUtils.findMethod(authServiceInstance.getClass(), "isRemoteUser");
        this.clearSessionMethod = ReflectionUtils.findMethod(authServiceInstance.getClass(), "clearSession", HttpServletRequest.class, HttpServletResponse.class);
        this.propertiesMethod = ReflectionUtils.findMethod(authServiceInstance.getClass(), "properties");
    }

    @Override
    public String type() {
        return (String) ReflectionUtils.invokeMethod(typeMethod, authServiceInstance);
    }

    @Override
    public Map<String, String> getSession(HttpServletRequest request) {
        Map<String, String> sessionMap = new HashMap<>();
        if (Objects.nonNull(getSessionMethod)) {
            sessionMap = (Map<String, String>) ReflectionUtils.invokeMethod(getSessionMethod, authServiceInstance, request);
            if (Objects.nonNull(sessionMap)) {
                UserContext.UserInfo userInfo = new UserContext.UserInfo();
                userInfo.setUserId(sessionMap.get(SESSION_USERID_STR));
                userInfo.setUserName(sessionMap.get(SESSION_USERNAME_STR));
                userInfo.setNickName(sessionMap.get(SESSION_NICKNAME_STR));
                userInfo.setSource(sessionMap.get(SESSION_SOURCE_STR));
                userInfo.setEmail(sessionMap.get(SESSION_EMAIL_STR));
                userInfo.setPhone(sessionMap.get(SESSION_PHONE_STR));
                userInfo.setExtraInfo(sessionMap.get(SESSION_EXTRA_STR));
                if (Objects.isNull(userInfo.getUserId())) {
                    UserContext.setCurrentUserInfo(null);
                } else {
                    UserContext.setIfRemoteUserCenter(isRemoteUser());
                    UserContext.setCurrentUserInfo(userInfo);
                }
            }
        }
        return sessionMap;
    }

    @Override
    public boolean match(HttpServletRequest request) {
        if (Objects.nonNull(matchMethod)) {
            return (boolean) ReflectionUtils.invokeMethod(matchMethod, authServiceInstance, request);
        }
        return false;
    }

    @Override
    public boolean isRemoteUser() {
        if (Objects.nonNull(isRemoteUserMethod)) {
            return (boolean) ReflectionUtils.invokeMethod(isRemoteUserMethod, authServiceInstance);
        }
        return false;
    }

    @Override
    public void clearSession(HttpServletRequest request, HttpServletResponse response){
        if (Objects.nonNull(clearSessionMethod)) {
            ReflectionUtils.invokeMethod(clearSessionMethod, authServiceInstance, request, response);
        }
    }

    @Override
    public Map<String, String> properties() {
        if (Objects.nonNull(propertiesMethod)) {
             return (Map<String, String>) ReflectionUtils.invokeMethod(propertiesMethod, authServiceInstance);
        }
        return new HashMap<>();
    }
}
