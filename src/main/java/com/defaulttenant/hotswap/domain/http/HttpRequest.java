package com.defaulttenant.hotswap.domain.http;

import org.apache.commons.io.IOUtils;
import com.defaulttenant.hotswap.util.NetWorkUtils;
import com.defaulttenant.hotswap.util.JacksonUtils;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * 对于request的抽象
 */
public class HttpRequest<T> {
    private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    /**
    * url
    */
    public String requestURL;

    /**
    * 请求路径
    */
    public String requestPath;

    /**
     * 请求的远程ip地址
     */
    public String remoteIp;

    /**
    * 请求方法
    */
    public String requestMethod;

    /**
     * 请求头
     */
    public Map<String,String> headers = new HashMap<>();

    /**
     * 请求体
     */
    public T body;


    public String getRequestURL() {
        return requestURL;
    }

    public HttpRequest<T> setRequestURL(String requestURL) {
        this.requestURL = requestURL;
        return this;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public HttpRequest<T> setPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public HttpRequest<T> setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public HttpRequest<T> setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public T getBody() {
        return body;
    }

    public HttpRequest<T> setBody(T body) {
        this.body = body;
        return this;
    }

    /**
     * 请求的-query-string
     */
    public Map<String,String> queryParams = new HashMap<>();
    /**
     * 请求路径的参数
     */
    public Map<String, String> pathParams = new HashMap<>();

    /**
     * cookie
     */
    public Map<String, HttpCookie> cookies = new HashMap<>();

    /**
     * 鉴权参数
     */
    public Map<String, HttpParameter> authParams = new HashMap<>();


    public HttpRequest<T> addHeader(String key, Object value) {
        if (null == value) {
            return this;
        }
        this.headers.put(key, String.valueOf(value));
        return this;
    }

    public <V> HttpRequest<T> addQueryParam(final String name, final String value) {
        this.queryParams.put(name, value);
        return this;
    }

    public <V> HttpRequest<T> addPathParam(final String name, final String value) {
        this.pathParams.put(name, value);
        return this;
    }

    public <V> HttpRequest<T> addAuthParam(final String name, final V value) {
        this.authParams.put(name, new HttpParameter<V>().setName(name).setValue(value));
        return this;
    }


    /**
     * 该方法主要给filter使用
     * @param request
     * @return
     * @param <T>
     */
    public static <T> HttpRequest<T> of(HttpServletRequest request) {
        HttpRequest httpRequest = new HttpRequest();
        /**
         * 这部分是不包含queryString的
         */
        httpRequest.requestURL = request.getRequestURL().toString();
        /**
         * 处理queryString
         */
        String queryString = request.getQueryString();
        if (!StringUtils.isEmpty(queryString)) {
            String[] queryStringArray = queryString.split("&");
            for (String temp : queryStringArray) {
                String[] queryStringInner = temp.split("=");
                if (queryStringInner.length < 2) {
                    continue;
                }
                httpRequest.queryParams.put(queryStringInner[0], queryStringInner[1]);
            }
        }
        /**
         * 处理header
         */
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String headerName = enumeration.nextElement();
            httpRequest.headers.put(headerName, request.getHeader(headerName));
        }
        /**
         * 处理body, Filter中暂时无需处理body
         */
        return httpRequest;
    }


    public static <T> HttpRequest createFormHttpServletRequest(HttpServletRequest request, Class<T> clazz) throws IOException {
        HttpRequest<T> httpRequest = new HttpRequest<>();
        httpRequest.remoteIp = NetWorkUtils.getCurrentIp(request);
        httpRequest.requestMethod =request.getMethod();
        httpRequest.requestPath = request.getRequestURI();
        httpRequest.requestURL = request.getRequestURL().toString();
        //headers
        Map<String,String> headerMap = new HashMap<>();

        Enumeration<String> headerEnumeration = request.getHeaderNames();
        while (headerEnumeration.hasMoreElements()){
            String headerKey = headerEnumeration.nextElement();
            headerMap.put(headerKey,request.getHeader(headerKey));
        }
        httpRequest.headers = headerMap;

        //querys
        Map<String,String> queryMap = new HashMap<>();
        Set<String> queryKeySet = request.getParameterMap().keySet();
        Iterator<String> iterator = queryKeySet.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            queryMap.put(key, request.getParameterMap().get(key)[0]);
        }
        httpRequest.queryParams = queryMap;

        //cookies
        Map<String,HttpCookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (int i = 0; i < cookies.length; i++) {
                HttpCookie httpCookie = new HttpCookie();
                httpCookie.name = cookies[i].getName();;
                httpCookie.value = cookies[i].getValue();
                httpCookie.cookiePath = cookies[i].getPath();
                httpCookie.domain = cookies[i].getDomain();
                httpCookie.httpOnly = cookies[i].isHttpOnly();
                httpCookie.maxAge = (long) cookies[i].getMaxAge();
                httpCookie.secure = cookies[i].getSecure();
                cookieMap.put(cookies[i].getName(),httpCookie);
            }
        }
        httpRequest.cookies =cookieMap;
        //body
        String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        if(!StringUtils.isEmpty(requestBody)){
            try {
                httpRequest.body = JacksonUtils.fromJson(requestBody, clazz);
            } catch (Exception e){
                httpRequest.body = handleBodyParseError(requestBody, clazz);
            }
        }
        return httpRequest;
    }

    private static <T> T handleBodyParseError(String requestBody, Class<T> clazz) {
        try{
            //非json格式
            if(clazz.isAssignableFrom(Integer.class)){
                return (T)Integer.valueOf(requestBody);
            } else if(clazz.isAssignableFrom(Long.class)) {
                return (T) Long.valueOf(requestBody);
            } else if(clazz.isAssignableFrom(Double.class)) {
                return (T) Double.valueOf(requestBody);
            } else if(clazz.isAssignableFrom(Boolean.class)) {
                return (T) Boolean.valueOf(requestBody);
            } else if (clazz.isAssignableFrom(String.class)){
                return (T) requestBody;
            } else if (clazz.isAssignableFrom(java.math.BigDecimal.class)){
                return (T) new java.math.BigDecimal(requestBody);
            } else if (clazz.isAssignableFrom(java.time.LocalDate.class)){
                return (T) java.time.LocalDate.parse(requestBody);
            } else if (clazz.isAssignableFrom(java.time.LocalTime.class)){
                return (T) java.time.LocalTime.parse(requestBody);
            } else if (clazz.isAssignableFrom(java.time.ZonedDateTime.class)){
                return (T) java.time.ZonedDateTime.parse(requestBody);
            } else{
                logger.error("不支持的body类型,{}",clazz);
            }
        } catch (Exception ignoreException){
            logger.error("不支持的body类型,{}",clazz);
        }

        return null;
    }

}
