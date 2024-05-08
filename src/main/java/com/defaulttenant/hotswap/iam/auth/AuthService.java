package com.defaulttenant.hotswap.iam.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * 低代码认证服务
 *
 * @author system
 */
public interface AuthService {

    /**
     * 识别当前用户身份
     *
     * @param request
     * @return map 默认应该包括 userId,userName,Phone,Email,NickName,Source
     */
    Map<String, String> getSession(HttpServletRequest request);

    /**
     * 当前会话是否匹配当前认证服务
     *
     * @param request
     * @return
     */
    boolean match(HttpServletRequest request);

    /**
    * 是否为远程用户管理或不需要用户管理
    * 默认为false
    * @return
    */
    default boolean isRemoteUser(){return false;}

    /**
     * 注销登出
     *
     * @param request
     * @param response
     */
    void clearSession(HttpServletRequest request, HttpServletResponse response);

    /**
     * 认证类型
     *
     * @return
     */
    String type();
    /**
    * 配置，可返回为空，预留方法
    * @return Map
    */
    default Map<String,String> properties(){return new HashMap<>();}
}
