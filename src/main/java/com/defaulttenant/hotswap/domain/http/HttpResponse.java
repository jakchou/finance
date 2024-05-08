package com.defaulttenant.hotswap.domain.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServerHttpResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * http响应抽象
 */
public class HttpResponse<T> {
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 响应头
     */
    public Map<String,String> headers = new HashMap<>();

    /**
     * 响应结果
     */
    public T body;

    /**
     * cookie
     */
    public Map<String, HttpCookie> cookies = new HashMap<>();

    /**
     * http响应码，用 Long类型是因为前端nasl没有int类型
     */
    public Long status;

    public void copyProperties(HttpResponse httpResponse, ServerHttpResponse servletResponse)  {
        HttpHeaders headers = servletResponse.getHeaders();
        //headers
        Map<String,String> headerMap = httpResponse.headers;
        if(MapUtils.isNotEmpty(headerMap)){
            headerMap.keySet().stream().forEach(key-> {
                headers.add(key,headerMap.get(key));
            });
        }
        //cookie
        Map<String,HttpCookie> cookieMap = httpResponse.cookies;
        if(MapUtils.isNotEmpty(cookieMap)){
            cookieMap.keySet().stream().forEach(key-> {
            HttpCookie httpCookie = cookieMap.get(key);
            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(httpCookie.name, httpCookie.value);
            setCookie(httpCookie, cookieBuilder);
            headers.add(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
           });
         }
        //status
        if(Objects.nonNull(httpResponse.status)){
            servletResponse.setStatusCode(HttpStatus.valueOf(Math.toIntExact(httpResponse.status)));
        }
    }
    public void copyProperties(HttpResponse httpResponse, HttpServletResponse servletResponse)  {
        //headers
        Map<String,String> headerMap = httpResponse.headers;
        if(MapUtils.isNotEmpty(headerMap)){
            headerMap.keySet().stream().forEach(key-> {
            servletResponse.addHeader(key,headerMap.get(key));
            });
        }
        //cookie
        Map<String,HttpCookie> cookieMap = httpResponse.cookies;
        if(MapUtils.isNotEmpty(cookieMap)){
            cookieMap.keySet().stream().forEach(key-> {
            HttpCookie httpCookie = cookieMap.get(key);
            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(httpCookie.name, httpCookie.value);
            setCookie(httpCookie, cookieBuilder);
            servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
            });
        }
        //status
        if(Objects.nonNull(httpResponse.status)){
        servletResponse.setStatus(Math.toIntExact(httpResponse.status));
        }
    }

    private void setCookie(HttpCookie httpCookie, ResponseCookie.ResponseCookieBuilder cookieBuilder) {
        if (StringUtils.isNotEmpty(httpCookie.domain)) {
             cookieBuilder.domain(httpCookie.domain);
        }
        if (StringUtils.isNotEmpty(httpCookie.cookiePath)) {
            cookieBuilder.path(httpCookie.cookiePath);
        }
        if (Objects.nonNull(httpCookie.maxAge)) {
            cookieBuilder.maxAge(httpCookie.maxAge);
        }
        if (Objects.nonNull(httpCookie.httpOnly)) {
            cookieBuilder.httpOnly(httpCookie.httpOnly);
        }
        if (Objects.nonNull(httpCookie.secure)) {
            cookieBuilder.secure(httpCookie.secure);
        }
        if (Objects.nonNull(httpCookie.sameSite)) {
            cookieBuilder.sameSite(httpCookie.sameSite);
        }
    }

    /**
     * http-response的响应
     * @param response
     * @throws IOException
     */
    public void writer(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(this.status.intValue());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.addHeader(entry.getKey(), entry.getValue());
        }
        if (null != response) {
            response.getWriter().write(objectMapper.writeValueAsString(body));
        }
    }
}
