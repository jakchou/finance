package com.defaulttenant.hotswap.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.context.UserContext;
import com.defaulttenant.hotswap.domain.enumeration.ErrorCodeEnum;
import com.defaulttenant.hotswap.service.system.UserServiceFactory;
import com.defaulttenant.hotswap.util.FileUploadUtils;
import com.defaulttenant.hotswap.web.ApiReturn;
import com.defaulttenant.hotswap.service.system.UserServiceFactory;
import com.netease.cloud.nuims.auth.api.bean.AuthInfo;
import com.netease.cloud.nuims.auth.domain.authen.AuthService;
import com.netease.cloud.nuims.plugin.starter.PluginConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class AuthFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    private AntPathMatcher matcher = new AntPathMatcher();
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<String> apis = new ArrayList<String>();
    private List<Pattern> patterns = new ArrayList<>();
    private MessageSource messageSource;
    private UserServiceFactory userServiceFactory;

    /**
     * 上传接口是否需要强制鉴权
     */
    private Boolean uploadApiAuthFlag;

    /**
    * 非系統API
    */
    public static final String[] systemExcludeAPIs = new String[]{
        Constants.AUTH_FILTER_API_PROCESS,
        Constants.AUTH_FILTER_API_USER_LIST,
        Constants.AUTH_FILTER_API_ANNOTATION_LIST
    };

    private AuthService authService;
    private PluginConfigProperties pluginConfigProperties;

    private static final String NORMAL_LOGIN_TYPE = "Normal";

    public AuthFilter(List<String> noAuthPaths, MessageSource messageSource, UserServiceFactory userServiceFactory, AuthService authService, PluginConfigProperties pluginConfigProperties, Boolean uploadApiAuthFlag) {
        noAuthPaths2Pattern(noAuthPaths);
        this.userServiceFactory = userServiceFactory;
        this.messageSource = messageSource;
        this.authService = authService;
        this.pluginConfigProperties = pluginConfigProperties;
        this.uploadApiAuthFlag = uploadApiAuthFlag;
    }


    private void noAuthPaths2Pattern(List<String> noAuthPaths) {
        for (String noAuthPathAndMethod : noAuthPaths) {
            // 处理以/id结尾的通用路径场景 /api/get/id:GET   -->  /api/get/[0-9a-zA-Z]+:GET
            if (noAuthPathAndMethod.contains(Constants.AUTH_FILTER_ID_SUFFIX)) {
                noAuthPathAndMethod = noAuthPathAndMethod.replace(Constants.AUTH_FILTER_ID_SUFFIX, "/[0-9a-zA-Z]+:");
            }
            Pattern pattern = Pattern.compile(noAuthPathAndMethod);
            patterns.add(pattern);
        }
    }

    private boolean needAuthApi(String path, String method){
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        // path不是以/api开头，不需要鉴权
        if(!path.startsWith(Constants.AUTH_API_PREFIX)){
            return false;
        }
        // path以/api/system 开头且不在systemExcludeAPIs里面, 不需要鉴权
        if((path.equals("/api/system") || path.startsWith("/api/system/")) && Arrays.stream(systemExcludeAPIs).noneMatch(s -> path.equals(s) || path.startsWith(s))){
            return false;
        }
        // 在白名单里面，不需要鉴权
        return !CollectionUtils.isEmpty(patterns) && patterns.stream().noneMatch(pattern->pattern.matcher(path + ":" + method).matches());
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        path = path.substring(contextPath.length());
        String method = request.getMethod().toUpperCase();
        if (path.startsWith("/upload") &&"POST".equals(method) && uploadApiAuthFlag) {
            // 上传接口需要鉴权
            if (isUserNotLogin(request)) {
                processNotLoginResponse(path, servletResponse);
                return;
            }
        } else if (!Objects.isNull(UserContext.getIfRemoteUserCenter()) && UserContext.getIfRemoteUserCenter()) {
            if (path.startsWith(Constants.AUTH_API_PREFIX) && Objects.isNull(UserContext.getCurrentUserInfo())) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setContentType(Constants.AUTH_FILTER_HEADER_CONTENT_TYPE);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                ApiReturn<Object> apiReturn = ApiReturn.of("", HttpStatus.UNAUTHORIZED.value(), ErrorCodeEnum.TOKEN_INVALID.code);
                response.getWriter().write(objectMapper.writeValueAsString(apiReturn));
                return;
            }
        } else if (needAuthApi(path, method)) {
            // 以/api开头的路径
            // 检验用户是否登录或Token校验不通过
            if (isUserNotLogin(request)) {
                processNotLoginResponse(path, servletResponse);
                return;
            }
        } else if (FileUploadUtils.isUploadApiPath(path, method)) {
            if (FileUploadUtils.shouldAccessControl(path) && isUserNotLogin(request)) {
                // 访问上传的文件，如果文件是需要登录才能访问的则需要鉴权，鉴权不通过返回默认错误提示图片
                servletRequest.setAttribute(Constants.ERROR_ATTR_PIC, "/file-no-auth.png");
                request.getRequestDispatcher("/error").forward(servletRequest, servletResponse);
                return;
            }

            if (FileUploadUtils.isPathExpiration(path)) {
                // 访问上传的文件，如果文件已过期则返回默认的错误图片
                servletRequest.setAttribute(Constants.ERROR_ATTR_PIC, "/file-expiration.png");
                request.getRequestDispatcher("/error").forward(servletRequest, servletResponse);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isUserNotLogin(HttpServletRequest servletRequest) {
        String accessToken = servletRequest.getHeader(Constants.AUTH_FILTER_HEADER_AUTHORIZATION);
        if (StringUtils.isEmpty(accessToken)) {
            // get from cookie if header not exist
            Cookie[] cookies = servletRequest.getCookies();
            if (null != cookies) {
                for (Cookie cookie : cookies) {
                    if (Constants.AUTH_FILTER_HEADER_AUTHORIZATION.equalsIgnoreCase(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return Objects.isNull(UserContext.getCurrentUserInfo()) ||
                !userServiceFactory.getTargetService().verifyWrapperToken(servletRequest, accessToken);
    }

    private void processNotLoginResponse(String path, ServletResponse servletResponse) throws IOException {
        if (path.startsWith("/api/user/system/getUser") || path.startsWith(Constants.AUTH_FILTER_API_ANNOTATION_LIST)) {
            processNotLoginResponseV2(servletResponse);
        } else {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setContentType(Constants.AUTH_FILTER_HEADER_CONTENT_TYPE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ApiReturn<Object> apiReturn = ApiReturn.of("", HttpStatus.UNAUTHORIZED.value(),
                    messageSource.getMessage(ErrorCodeEnum.NO_PERMISSION_ACCESS_RESOURCE.code,
                            null, ErrorCodeEnum.NO_PERMISSION_ACCESS_RESOURCE.code, LocaleContextHolder.getLocale()));
            response.getWriter().write(objectMapper.writeValueAsString(apiReturn));
        }
    }

    private void processNotLoginResponseV2(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType(Constants.AUTH_FILTER_HEADER_CONTENT_TYPE);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write("");
    }

    @Override
    public void destroy() {
        log.info("destroy auth filter ...");
    }

}