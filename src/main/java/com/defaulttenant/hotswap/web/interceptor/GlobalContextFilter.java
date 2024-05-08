package com.defaulttenant.hotswap.web.interceptor;

import com.defaulttenant.hotswap.util.GlobalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.ZoneId;

public class GlobalContextFilter implements Filter {
    private final Logger log = LoggerFactory.getLogger(GlobalContextFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("init globalContext filter ...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String accessToken = request.getHeader("authorization");
            String domainName = request.getHeader("DomainName");
            String timeZone = request.getHeader("TimeZone");
            if (!StringUtils.isEmpty(accessToken)) {
                GlobalContext.setToken(accessToken);
            }
            if (!StringUtils.isEmpty(domainName)) {
                GlobalContext.setDomainName(domainName);
            }
            if (!StringUtils.isEmpty(timeZone)) {
                GlobalContext.setTimeZone(timeZone);
            } else{
                GlobalContext.setTimeZone(ZoneId.systemDefault().getId());
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            GlobalContext.clear();
        }
    }

    @Override
    public void destroy() {
        log.debug("destroy globalContext filter ...");
    }
}
