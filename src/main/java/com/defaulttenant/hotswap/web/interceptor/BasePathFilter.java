package com.defaulttenant.hotswap.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.defaulttenant.hotswap.config.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class BasePathFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(BasePathFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            String requestPath = request.getServletPath();
            String basePath = "";
            if (StringUtils.isNotBlank(requestPath)) {
                String[] splitUri = requestPath.split("/");
                if (splitUri.length >= 2) {
                    basePath = "/" + requestPath.split("/")[1];
                }
            }
            servletRequest.setAttribute(Constants.LCAP_INFER_BASEPATH, basePath);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.error("获取basePath 失败, uri: {}, error: {}", request.getRequestURI(), e.getMessage());
        }
    }
}
