package com.defaulttenant.hotswap.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Map;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author sys
 */
public class RepeatedHttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepeatedHttpServletRequestWrapper.class);
    private byte[] cachedBody;

    private final Map<String, String[]> parameterMap;

    public RepeatedHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);

        this.parameterMap = new HashMap<>(request.getParameterMap());

        // 如果请求是表单提交，解析并保存表单参数
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            String body = new String(cachedBody, request.getCharacterEncoding());
            String[] parameters = body.split("&");
            for (String parameter : parameters) {
                String[] keyValue = parameter.split("=");
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0], request.getCharacterEncoding());
                    String value = URLDecoder.decode(keyValue[1], request.getCharacterEncoding());
                    if (parameterMap.containsKey(key)) {
                        List<String> values = new ArrayList<>(Arrays.asList(parameterMap.get(key)));
                        values.add(value);
                        parameterMap.put(key, values.toArray(new String[0]));
                    } else {
                        parameterMap.put(key, new String[]{value});
                    }
                }
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }
    public static class CachedBodyServletInputStream extends ServletInputStream {
        private InputStream cachedBodyInputStream;
        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (IOException e) {
                LOGGER.error("cachedBodyInputStream error={}",e.getMessage());
            }
            return false;
        }
        @Override
        public boolean isReady() {
            return true;
        }
        @Override
        public void setReadListener(ReadListener readListener) {
            // do nothing
        }

    }
    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameterMap.get(name);
        return (values != null && values.length > 0) ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }
}
