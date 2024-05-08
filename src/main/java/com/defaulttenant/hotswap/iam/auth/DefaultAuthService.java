package com.defaulttenant.hotswap.iam.auth;

import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.context.UserContext;
import com.defaulttenant.hotswap.service.system.UserServiceFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认认证服务
 * @author system
 */
@Service
public class DefaultAuthService implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthService.class);
    @Resource
    private UserServiceFactory userServiceFactory;
    @Resource
    private com.netease.cloud.nuims.auth.domain.authen.AuthService pluginAuthService;
    @Resource
    private  com.netease.cloud.nuims.auth.domain.authen.TokenService tokenService;
    @Override
    public Map<String, String> getSession(HttpServletRequest request) {
        Map<String,String> sessionMap = new HashMap<>();
        String accessToken = request.getHeader(Constants.AUTH_FILTER_HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(accessToken)) {
            // get from cookie if header not exist
            Cookie[] cookies = request.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if (Constants.AUTH_FILTER_HEADER_AUTHORIZATION.equalsIgnoreCase(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        try {
        Map<String, Object> res = userServiceFactory.getTargetService().authentication(request, accessToken);
        if (Constants.AUTH_FILTER_SUCCESS_STR.equals(res.get(Constants.AUTH_FILTER_CODE_STR))) {
            HashMap<String, Object> dataMap = (HashMap<String, Object>) res.get(Constants.AUTH_FILTER_DATA_STR);
            UserContext.UserInfo userInfo = new UserContext.UserInfo();
            userInfo.setUserId((String) dataMap.get(Constants.AUTH_FILTER_USERID_STR));
            userInfo.setUserName((String) dataMap.get(Constants.AUTH_FILTER_USERNAME_STR));
            userInfo.setPhone((String) dataMap.get(Constants.AUTH_FILTER_PHONE_STR));
            userInfo.setEmail((String) dataMap.get(Constants.AUTH_FILTER_EMAIL_STR));
            userInfo.setStatus((String) dataMap.get(Constants.AUTH_FILTER_STATUS_STR));
            userInfo.setCreateTime((Long) dataMap.get(Constants.AUTH_FILTER_CREATETIME_STR));
            userInfo.setUpdateTime((Long) dataMap.get(Constants.AUTH_FILTER_UPDATETIME_STR));
            userInfo.setNickName((String) dataMap.get(Constants.AUTH_FILTER_NICKNAME_STR));
            userInfo.setSource((String) dataMap.get(Constants.AUTH_FILTER_SOURCE_STR));
            UserContext.setCurrentUserInfo(userInfo);
            UserContext.setIfRemoteUserCenter(false);
        }
        } catch (Exception e) {
             LOGGER.error("Error: {}", e.getMessage());
        }
        return sessionMap;
    }

    @Override
    public boolean match(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (Constants.AUTH_FILTER_HEADER_AUTHORIZATION.equalsIgnoreCase(cookie.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void clearSession(HttpServletRequest request, HttpServletResponse response) {
        try {
            String authorization = request.getHeader(Constants.AUTH_FILTER_HEADER_AUTHORIZATION);
            if (StringUtils.isEmpty(authorization)) {
                Cookie[] cookies = request.getCookies();
                if (ArrayUtils.isNotEmpty(cookies)) {
                    for (Cookie cookie : cookies) {
                        if (Constants.AUTH_FILTER_HEADER_AUTHORIZATION.equalsIgnoreCase(cookie.getName())) {
                            authorization = cookie.getValue();
                            cookie.setMaxAge(0);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                            break;
                        }
                    }
                }
            }
            //兼容原有的插件方式
            if (StringUtils.isNotEmpty(authorization)) {
                String authType = tokenService.getTokenValue(authorization, com.netease.cloud.nuims.auth.common.constants.Constants.AUTH_TYPE_KEY);
                if(com.netease.cloud.nuims.auth.domain.authen.NormalAuthService.NAME.equals(authType) || pluginAuthService.containService(authType)){
                    String authParam = tokenService.getTokenValue(authorization, Constants.THIRD_PARTY_COOKIE_BODY_KEY);
                    pluginAuthService.logout(request, authType, authParam);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error: {}", e.getMessage());
        }
    }

    @Override
    public String type() {
        return AuthManager.TYPE_DEFAULT + "SYSTEM";
    }
}
