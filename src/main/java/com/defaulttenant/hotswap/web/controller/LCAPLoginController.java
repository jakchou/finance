package com.defaulttenant.hotswap.web.controller;

import com.defaulttenant.hotswap.web.ApiReturn;
import com.defaulttenant.hotswap.iam.auth.AuthManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import com.netease.cloud.nuims.plugin.starter.PluginConfigProperties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


/**
 * auto generate LCAPLoginController
 *
 * @author sys
 * 
 */
@RestController
@RequestMapping("api/system")
public class LCAPLoginController {

    /**
    * 已开启的认证方式
    */
    public String authTypes = "Normal";

    public static final String ERROR_MESSAGE = "登录能力缺失，无对应LCAPUser实体";

    @Resource
    private AuthManager authManager;

    private final Logger log = LoggerFactory.getLogger(LCAPLoginController.class);

    @Resource
    private PluginConfigProperties pluginConfigProperties;
    /**
    * 获取认证相关参数
    * @return
    */
    @GetMapping("getAppLoginTypes")
    public ApiReturn getAppLoginTypes() {
        Map<String, PluginConfigProperties.Plugin> unionAuthTypes = this.pluginConfigProperties.getList();
        //开启的认证方式
        String[] alreadyOpenAuthTypes = authTypes.split(",");
        for (int i = 0; i < alreadyOpenAuthTypes.length; i++) {
        String authType = alreadyOpenAuthTypes[i];
            unionAuthTypes.putIfAbsent(authType, null);
        }
        return ApiReturn.of(unionAuthTypes, HttpStatus.OK.value());
    }

    /**
    * 认证统一入口
    *
    * @return
    */
    @PostMapping("login")
    public ResponseEntity<ApiReturn> login() {
        return new ResponseEntity<>(ApiReturn.of("", HttpStatus.FORBIDDEN.value(), ERROR_MESSAGE), HttpStatus.FORBIDDEN);
    }


    /**
    * 注销
    *
    * @return
    */
    @GetMapping("logout")
    public ResponseEntity<ApiReturn> logout(HttpServletRequest request, HttpServletResponse response){
        List<com.defaulttenant.hotswap.iam.auth.AuthService> matchedAuthServiceList = authManager.getAuthService(request);
        if (CollectionUtils.isNotEmpty(matchedAuthServiceList)) {
            for (int i = 0; i < matchedAuthServiceList.size(); i++) {
                com.defaulttenant.hotswap.iam.auth.AuthService authService = matchedAuthServiceList.get(i);
                try {
                    authService.clearSession(request,response);
                } catch (Exception e) {
                    log.error("Error: {}", e.getMessage());
                    return new ResponseEntity<>(ApiReturn.of("", HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.FORBIDDEN);
                }
            }
        }
        return new ResponseEntity<>(ApiReturn.of("", HttpStatus.OK.value()), HttpStatus.OK);
     }
}