package com.defaulttenant.hotswap.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.config.LcpProperties;
import com.defaulttenant.hotswap.service.ServiceVersion;
import com.defaulttenant.hotswap.service.system.UserProxyService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;


@Service
@ServiceVersion
public class NuimsUserService implements UserProxyService {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    private LcpProperties lcpProperties;
    @Resource
    private RestTemplate restTemplate;


    @Override
    public Map authentication(HttpServletRequest request, String token) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(Constants.AUTH_FILTER_HEADER_CONTENT_TYPE));
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            headers.add(key, value);
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        String url = String.format("%s?Action=GetUser&Version=2020-06-01",
                lcpProperties.getNuims() + Constants.AUTH_FILTER_API_NUIMS);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Map.class);
        return responseEntity.getBody();
    }

    @Override
    public boolean verifyWrapperToken(HttpServletRequest request, String token) {
        //默认返回true,Nuims校验过程在authentication方法里
        return true;
    }

}
