package com.defaulttenant.hotswap.web.interceptor;

import org.springframework.http.MediaType;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.defaulttenant.hotswap.config.LcpProperties;
import com.defaulttenant.hotswap.util.TraceIdHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * The Filter to record access log
 *
 * @author sys
 * @date 2022-03-22 14:58
 */
public class AccessLogFilter extends CommonsRequestLoggingFilter {
    private LcpProperties lcpProperties;
    public AccessLogFilter(LcpProperties lcpProperties) {
        setIncludeQueryString(true);
        setIncludePayload(true);
        this.lcpProperties = lcpProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;
        if (Boolean.TRUE.equals(lcpProperties.getLogResponse())) {
            responseToUse = new CachingResultResponseWrapper(response);
        }

        if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper) &&
                !(request instanceof MultipartHttpServletRequest) && Boolean.TRUE.equals(lcpProperties.getLogRequest())) {
            requestToUse = new ContentCachingRequestWrapper(request, 4096);
        }

        if (isFirstRequest) {
            beforeRequest(requestToUse);
        }

        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (!isAsyncStarted(requestToUse)) {
                afterRequest(requestToUse, responseToUse);
            }
        }
    }

    private void beforeRequest(HttpServletRequest request) {
        String prefix = "Before request " + TraceIdHolder.getTraceId() + " [";
        String suffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;
        logger.info(createMessage(request, prefix, suffix));
    }

    private void afterRequest(HttpServletRequest request, HttpServletResponse response) {
        String prefix = "After request " + TraceIdHolder.getTraceId() + " [";
        String suffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;
        if (response instanceof CachingResultResponseWrapper) {
            suffix = " result=" + ((CachingResultResponseWrapper) response).getCacheResult() + "]";
        }

        logger.info(createMessage(request, prefix, suffix));
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        return super.createMessage(request, prefix, suffix);
    }

    public static class CachingResultResponseWrapper extends HttpServletResponseWrapper {
        private FastByteArrayOutputStream cacheOutputStream = new FastByteArrayOutputStream();
        private ServletOutputStream servletOutputStream;

        public CachingResultResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public String getCacheResult() {
            return cacheOutputStream.toString();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            ServletOutputStream oriServletOutputStream = super.getOutputStream();
            if (getContentType().equals(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
                servletOutputStream = oriServletOutputStream;
            } else if (null == servletOutputStream) {
                servletOutputStream = new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        oriServletOutputStream.write(b);
                        cacheOutputStream.write(b);
                    }

                    @Override
                    public boolean isReady() {
                        return oriServletOutputStream.isReady();
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                        oriServletOutputStream.setWriteListener(writeListener);
                    }
                };
            }

            return servletOutputStream;
        }
    }
}
