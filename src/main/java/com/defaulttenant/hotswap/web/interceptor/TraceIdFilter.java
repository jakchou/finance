package com.defaulttenant.hotswap.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.defaulttenant.hotswap.util.TraceIdHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;

public class TraceIdFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TraceIdFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("init TraceIdFilter...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String uniqueId = UUID.randomUUID().toString();
            StringBuilder uuidBuilder = new StringBuilder(TraceIdHolder.LOG_TRACE_PREFIX);
            uuidBuilder.append(uniqueId);
            MDC.put(TraceIdHolder.LOG_TRACE_KEY, uuidBuilder.toString());
            MDC.put(TraceIdHolder.TRACE_ID, uniqueId);
            TraceIdHolder.setTraceId(uniqueId);

            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.info("", e);
            throw e;
        } finally {
            TraceIdHolder.remove();
        }
    }

    @Override
    public void destroy() {
        logger.debug("destroy TraceIdFilter...");
        MDC.remove(TraceIdHolder.LOG_TRACE_KEY);
    }
}
