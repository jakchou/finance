package com.defaulttenant.hotswap.util;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 远程请求上下文
 */
public class RemoteCallContext {
    private List<String> queryString = Lists.newArrayList();
    private String url;
    private Boolean hasFormDataHeader = Boolean.FALSE;

    private boolean hasXmlHeader;

    private Object body;

    private HttpHeaders headers;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public List<String> getQueryString() {
        return queryString;
    }

    public RemoteCallContext setQueryString(List<String> queryString) {
        this.queryString = queryString;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public RemoteCallContext setUrl(String url) {
        this.url = url;
        return this;
    }

    public Boolean getHasFormDataHeader() {
        return hasFormDataHeader;
    }

    public RemoteCallContext setHasFormDataHeader(Boolean hasFormDataHeader) {
        this.hasFormDataHeader = hasFormDataHeader;
        return this;
    }

    public boolean isHasXmlHeader() {
        return hasXmlHeader;
    }

    public void setHasXmlHeader(boolean hasXmlHeader) {
        this.hasXmlHeader = hasXmlHeader;
    }

    public HttpEntity buildHttpEntity() throws InvocationTargetException, IllegalAccessException {
        if (this.hasFormDataHeader) {
            return new HttpEntity(this.buildFormDataParam(), this.headers);
        } else if (this.hasXmlHeader) {
            return new HttpEntity<>(this.buildXmlDataParam(), this.headers);
        }
        return new HttpEntity(this.body, this.headers);
    }

    private MultiValueMap<String, Object> buildFormDataParam() throws InvocationTargetException, IllegalAccessException {
        if (null == this.body) {
            return new LinkedMultiValueMap<>();
        }
        MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();
        Class<?> clazz = body.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.getName().startsWith("get")) {
                continue;
            }
            String filedName = StringUtils.uncapitalize(method.getName().substring(method.getName().indexOf("get") + 3));
            result.add(filedName, method.invoke(body));
        }
        return result;
    }


    /**
     * build xml type param
     *
     * @return xml type param
     */
    private String buildXmlDataParam() {
        if (null == this.body) {
            return "";
        }
        HttpHeaders httpHeaders = this.getHeaders();
        Charset charset = XmlUtils.getXmlCharset(httpHeaders.getContentType());
        return XmlUtils.beanToXmlWithCharset(body, charset);
    }

}
