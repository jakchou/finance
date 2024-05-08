package com.defaulttenant.hotswap.web.interceptor;

import com.defaulttenant.hotswap.context.UserContext;
import com.defaulttenant.hotswap.iam.auth.AuthManager;
import com.defaulttenant.hotswap.iam.auth.AuthService;
import com.defaulttenant.hotswap.util.GlobalContext;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import com.defaulttenant.hotswap.domain.http.HttpRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * 解析用戶信息，放置上下文中
 *
 * @author system
 */
public class UserContextFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(UserContextFilter.class);

    private AuthManager authManager;

    String[] excludeUrls = new String[]{"/management/health","/favicon.ico","/management/prometheus"};

    public UserContextFilter(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        if(Arrays.stream(excludeUrls).anyMatch(url->url.equals(requestURI))){
            chain.doFilter(request, servletResponse);
            return;
        }
        String contentType = request.getContentType();
        if (StringUtils.isNotBlank(contentType) && contentType.toLowerCase().startsWith("multipart/")) {
            request = new StandardServletMultipartResolver().resolveMultipart(request);
        }else {
            request = new RepeatedHttpServletRequestWrapper(request);
        }
        GlobalContext.initializingSessionVariables(request);
        try {
            List<AuthService> matchedAuthServiceList = authManager.getAuthService(request);
            if (CollectionUtils.isNotEmpty(matchedAuthServiceList)) {
                for (int i = 0; i < matchedAuthServiceList.size(); i++) {
                    AuthService authService = matchedAuthServiceList.get(i);
                    authService.getSession(request);
                    if (Objects.nonNull(UserContext.getCurrentUser())) {
                        GlobalContext.fillInCurrentUserInSessionVariable(UserContext.getCurrentUser().userId,UserContext.getCurrentUser().userName);
                        log.info("UserInfo解析成功，解析AuthService=[{}]，type=[{}], userIdValid=[{}]",  authService.getClass().getName(),authService.type(),Objects.nonNull(UserContext.getCurrentUser().userId));
                        break;
                    }
                }
            }
        } catch (Exception ignored) {
            log.error("userContextFilter handle exception:" + ignored.getMessage());
        }
        try {
            chain.doFilter(request, servletResponse);
        } finally {
            UserContext.clear();
            GlobalContext.clear();
        }
    }


}
