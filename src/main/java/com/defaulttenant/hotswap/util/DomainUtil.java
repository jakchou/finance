package com.defaulttenant.hotswap.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.defaulttenant.hotswap.config.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author wangshibo01
 */
public class DomainUtil {

    public static final String SCHEME_HTTP_PREFIX = "http://";
    public static final String SCHEME_HTTPS_PREFIX = "https://";

    private DomainUtil() {}


    /**
     * 匹配http://、https://开头的URL链接
     * 匹配链接： https://www.example.com、 http://www.example
     * 不匹配链接: www.example.com
     * @param serviceDomain
     * @return true or false
     */
    public static boolean matchSupportHttpProtocolPrefix(String serviceDomain){
        if(StringUtils.isBlank(serviceDomain)) {
            return false;
        }
        return serviceDomain.startsWith(SCHEME_HTTP_PREFIX) || serviceDomain.startsWith(SCHEME_HTTPS_PREFIX);
    }


    /**
     * 去除https或http
     * @param url
     * @return
     */
    public static String getHostFromUrl(String url){
        if(StringUtils.isNotEmpty(url)){
            return url.replace(SCHEME_HTTPS_PREFIX, "").replace(SCHEME_HTTP_PREFIX, "");
        }
        return url;
    }


    /**
     * 判断是不是本服务域下的url
     * @param url
     * @param request
     * @return
     */
    public static boolean isLocalDomain(String url, HttpServletRequest request) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        if (!DomainUtil.matchSupportHttpProtocolPrefix(url)) {
            //相对路径肯定是本域下的url
            return true;
        }

        if (url.startsWith("http://localhost") ||
                url.startsWith("https://localhost") ||
                url.startsWith("http://127.0.0.1") ||
                url.startsWith("https://127.0.0.1")) {
            // 回环ip肯定是本域
            return true;
        }

        if (StringUtils.isEmpty(request.getContextPath()) && !NetWorkUtils.isLocalHost(request.getServerName())) {
            String domain = request.getScheme() + "://" + request.getServerName() +
                    ( Constants.HTTP_DEFAULT_PORT == request.getServerPort() ? "" : ":" + request.getServerPort() );
            return url.startsWith(domain);
        } else if (!StringUtils.isEmpty(request.getContextPath())) {
            return url.startsWith(request.getContextPath());
        }

        return false;
    }

    public static boolean isLocalDomain(String url) {
        return isLocalDomain(url, ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    public static Map<String, String> getUrlQueryAsMap(String url){
        if(StringUtils.isNotEmpty(url)){
            return Collections.emptyMap();
        }

        String[] parts = StringUtils.split(url, "?", 2);

        if (parts.length < 2) {
            return Collections.emptyMap();
        }

        String queryParts = parts[1];
        String[] paramParts = StringUtils.split(queryParts, "&");

        Map<String, String> params = new HashMap<>(paramParts.length);
        for (String paramStr : paramParts) {
            String[] eachParamParts = paramStr.split("=", 2);
            if (eachParamParts.length < 2) {
                params.put(eachParamParts[0], null);
            } else {
                params.put(eachParamParts[0], eachParamParts[1]);
            }
        }
        return params;
    }
}
