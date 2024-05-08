package com.defaulttenant.hotswap.integration.http;

import com.defaulttenant.hotswap.config.JacksonConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.util.ExtendMappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.netease.cloud.RemoteCallApi;
import org.springframework.util.CollectionUtils;
import com.netease.cloud.RemoteCallRequest;
import com.netease.cloud.RemoteCallResponse;
import com.netease.cloud.RemoteCallApiClient;
import com.defaulttenant.hotswap.util.XmlMessageConverter;
import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

/**
 * http接口调用
 */
@RemoteCallApiClient
public class CallHttpApiService implements RemoteCallApi{

    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init(){
        HttpComponentsClientRestfulHttpRequestFactory httpRequestFactory = new HttpComponentsClientRestfulHttpRequestFactory();
        /**
         * 需要使用自定义的超时参数, 单位是秒
         */
        String proxyTime = environment.getProperty("custom.http_request_timeout");
        if (StringUtils.isEmpty(proxyTime)) {
            proxyTime = "30s";
        }
        Integer timeOut = Integer.parseInt(proxyTime.toLowerCase().split("s")[0]) * 1000;
        httpRequestFactory.setConnectionRequestTimeout(timeOut);
        httpRequestFactory.setConnectTimeout(timeOut);
        httpRequestFactory.setReadTimeout(timeOut);
        this.restTemplate = new RestTemplate(httpRequestFactory);
        // delete restTemplate`s xml converter,use custom xml convert
        this.restTemplate.getMessageConverters().removeIf(converter->(Objects.equals(MappingJackson2XmlHttpMessageConverter.class,converter.getClass())));
        this.restTemplate.getMessageConverters().add(new XmlMessageConverter<>());

        this.restTemplate.getMessageConverters().add(new ExtendMappingJackson2HttpMessageConverter());
        //需要替换ObjectMapper, 否则日期处理会有问题
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略对象中不存在的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule
                .addSerializer(LocalDate.class, new JacksonConfiguration.LcpLocalDateSerializer())
                .addDeserializer(LocalDate.class, new JacksonConfiguration.LcpLocalDateDeserializer())
                .addSerializer(ZonedDateTime.class, new JacksonConfiguration.LcpZonedDateTimeSerializer())
                .addDeserializer(ZonedDateTime.class, new JacksonConfiguration.LcpZonedDateTimeDeserializer());
        objectMapper.registerModule(javaTimeModule);
        this.restTemplate.getMessageConverters().stream().filter(e -> e instanceof MappingJackson2HttpMessageConverter)
                .map(e -> (MappingJackson2HttpMessageConverter) e).forEach(e -> e.setObjectMapper(objectMapper));
    }

    @Override
    public <T, R> RemoteCallResponse<R> call(RemoteCallRequest<T, R> request) {
        if (!(request instanceof HttpCallRequest)) {
            throw new RuntimeException(request.getClass().toString() + " cant cast to HttpCallRequest");
        }
        //构建httpEntity
        HttpCallRequest<T, R> httpCallRequest = (HttpCallRequest<T, R>) request;
        try {
            ResponseEntity<R> responseEntity;
            if (!CollectionUtils.isEmpty(httpCallRequest.getQueryString())) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(httpCallRequest.getUrl());
                for (Map.Entry<String, Object> entry : httpCallRequest.getQueryString().entrySet()) {
                    if (entry != null) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if (key == null || value == null) {
                            continue;
                        }
                        uriBuilder.queryParam(key, value);
                    }
                }
                httpCallRequest.setUrl(uriBuilder.toUriString());
            }
            if (null == httpCallRequest.getReturnClazz()) {
                responseEntity = restTemplate.exchange(
                        new URI(httpCallRequest.getUrl()),
                        httpCallRequest.getRequestMethod(),
                        httpCallRequest.buildHttpEntity(),
                        httpCallRequest.getCollectionReturnClazz()
                );
            } else {
                responseEntity = restTemplate.exchange(
                        new URI(httpCallRequest.getUrl()),
                        httpCallRequest.getRequestMethod(),
                        httpCallRequest.buildHttpEntity(),
                        httpCallRequest.getReturnClazz()
                );
            }
            return new HttpCallResponse<>(responseEntity.getBody());
        } catch (RestClientResponseException e) {
            throw new RuntimeException(e.getResponseBodyAsString(), e);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public String protocolType() {
        return "HTTP";
    }
}
