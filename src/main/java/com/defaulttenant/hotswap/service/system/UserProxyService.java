package com.defaulttenant.hotswap.service.system;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 用户代理服务，可以动态路由远程和本地用户中心
 */
public interface UserProxyService {
    /**
     * 解析Token
     * @param request
     * @param token
     * @return
     * @throws Exception
     */
    Map authentication(HttpServletRequest request, String token) throws Exception;

    /**
     * 如果存在关联token，则需校验
     * 默认不存在，即通过校验
     * @return
     */
    boolean verifyWrapperToken(HttpServletRequest request,String token);
}
