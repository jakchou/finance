package com.defaulttenant.hotswap.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.defaulttenant.hotswap.config.JacksonConfiguration;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import javax.annotation.Resource;
import javax.annotation.PostConstruct;

import com.defaulttenant.hotswap.exception.HttpCodeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.defaulttenant.hotswap.config.AppAddrProperties;
import com.defaulttenant.hotswap.config.Constants;

@Component
public class RemoteCall {
    private final Logger log = LoggerFactory.getLogger(RemoteCall.class);
    private RestTemplate restTemplate = getRestTemplate();
    public static RestTemplate getRestTemplate(){
        HttpComponentsClientRestfulHttpRequestFactory httpRequestFactory = new HttpComponentsClientRestfulHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(30000);
        httpRequestFactory.setConnectTimeout(30000);
        httpRequestFactory.setReadTimeout(30000);
        return new RestTemplate(httpRequestFactory);
    }
    private ObjectMapper objectMapper = new ObjectMapper();

    private final static String FORM_DATA_CONTENT_TYPE = "application/x-www-form-urlencoded";

    @Value("${spring.profiles.active:dev}")
    private String env;

    @Value("${lcp.flowUrl:UNKNOW}")
    private String flowUrl;

    @Value("${lcp.gatewayUrl:UNKNOW}")
    private String gateWayAddress;

    @Value("${lcp.gatewayType:qingzhou}")
    private String gatewayType;

    @Resource
    private AppAddrProperties appAddr;

    @PostConstruct
    public void init() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule
                .addSerializer(LocalDate.class, new JacksonConfiguration.LcpLocalDateSerializer())
                .addDeserializer(LocalDate.class, new JacksonConfiguration.LcpLocalDateDeserializer())
                .addSerializer(ZonedDateTime.class, new JacksonConfiguration.LcpZonedDateTimeSerializer())
                .addDeserializer(ZonedDateTime.class, new JacksonConfiguration.LcpZonedDateTimeDeserializer());
        objectMapper.registerModule(javaTimeModule);
        restTemplate.getMessageConverters().add(new ExtendMappingJackson2HttpMessageConverter());
        // delete restTemplate`s xml converter,use custom xml convert
        restTemplate.getMessageConverters().removeIf(converter->(Objects.equals(MappingJackson2XmlHttpMessageConverter.class,converter.getClass())));
        restTemplate.getMessageConverters().add(new XmlMessageConverter<>());
    }

    public void processStart(String processDefinitionKey, Object var) throws JsonProcessingException {
        Map<String, Object> processStartBody = new HashMap<>();
        processStartBody.put("processDefinitionKey", processDefinitionKey);
        processStartBody.put("returnVariables", true);
        processStartBody.put("variables", var);
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", GlobalContext.getToken());
        headers.add("domainname", GlobalContext.getDomainname());
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = objectMapper.writeValueAsString(processStartBody);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        String url = String.format("%s/bpms-rest/service/runtime/v2/process-instances", flowUrl);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        log.info("process start ：{}", responseEntity.getBody());
    }

    public void processTaskComplete(String taskId, Object var) throws JsonProcessingException {
        Map<String, Object> taskCompleteBody = new HashMap<>();
        taskCompleteBody.put("action", "complete");
        taskCompleteBody.put("variables", var);
        HttpHeaders headers = new HttpHeaders();
        headers.add("authorization", GlobalContext.getToken());
        headers.add("domainname", GlobalContext.getDomainname());
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = objectMapper.writeValueAsString(taskCompleteBody);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        String url = String.format("%s/bpms-rest/service/runtime/v2/tasks/%s", flowUrl, taskId);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        log.info("process task complete ：{}", responseEntity.getBody());
    }

    public <T> T call(String path, String httpMethod, ParameterizedTypeReference<T> clazz, Param... params) {
        RemoteCallContext context = this.buildContext(path, params);
        try {
            log.debug("class is {}", clazz);
            ResponseEntity<T> responseEntity = restTemplate.exchange(
                    context.getUrl(),
                    HttpMethod.resolve(httpMethod.toUpperCase()),
                    context.buildHttpEntity(),
                    clazz
            );
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getResponseBodyAsString(), e);
        } catch (Exception ex) {
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex);
        }
    }

    private RemoteCallContext buildContext(String path, Param... params) {
        RemoteCallContext context = new RemoteCallContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Env", env);
        String headerContent = "Content-type";
        Object body = null;
        for (Param p : params) {
            if ("body".equalsIgnoreCase(p.in)) {
                body = p.value;
            } else if ("path".equalsIgnoreCase(p.in)) {
                path = path.replace("{" + p.name + "}", null == p.value ? p.name : p.value.toString());
            } else {
                handleParam(context, p, headers);
            }
        }

        if (Constants.LIGHT_GW_TYPE.equals(gatewayType)) {
            String[] splits = path.split("/");
            boolean endWithTag = path.endsWith("/");
            if (splits.length > 2) {
                // 获取path路径中appName
                String appName = splits[2];
                gateWayAddress = appAddr.getAddr().get(appName);
                path = "/" + String.join("/", Arrays.copyOfRange(splits, 3, splits.length));
            }

            if (endWithTag) {
                path = path + "/";
            }
        }

        if (context.getQueryString().size() > 0) {
            path = path + "?" + StringUtils.join(context.getQueryString(), "&");
        }

        // 如果传过来的参数里有content-type就用参数里的 没传就手动添加一个默认值
        if (!headers.containsKey(headerContent)) {
            headers.add(headerContent, "application/json");
        }

        context.setUrl(gateWayAddress + path);
        context.setBody(body);
        context.setHeaders(headers);

        return context;
    }

    private void handleParam(RemoteCallContext context, Param p, HttpHeaders headers) {
        if ("query".equalsIgnoreCase(p.in)) {
            context.getQueryString().add(p.name + "=" + p.value);
        } else if ("header".equalsIgnoreCase(p.in)) {
            if (p.value.toString().contains(FORM_DATA_CONTENT_TYPE)) {
                context.setHasFormDataHeader(Boolean.TRUE);
            } else if (p.value.toString().contains(MediaType.TEXT_XML_VALUE) || p.value.toString().contains(MediaType.APPLICATION_XML_VALUE)) {
                // set xml header
                context.setHasXmlHeader(Boolean.TRUE);
            }
            headers.add(p.name, null == p.value ? null : p.value.toString());
        }
    }

    public <T> T call(String path, String httpMethod, Class<T> clazz, Param... params) {
        RemoteCallContext context = this.buildContext(path, params);
        try {
            log.debug("class is {}", clazz);
            ResponseEntity<T> responseEntity = restTemplate.exchange(
                    context.getUrl(),
                    HttpMethod.resolve(httpMethod.toUpperCase()),
                    context.buildHttpEntity(),
                    clazz
            );
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getResponseBodyAsString(), e);
        } catch (Exception ex) {
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ex);
        }
    }

    public <T> List<T> callList(String path, String httpMethod, Class<T> clazz, Param... params) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Env", env);
        List<String> query = new ArrayList<>();
        Object body = null;
        for (Param p : params) {
            if ("query".equalsIgnoreCase(p.in)) {
                query.add(p.name + "=" + p.value);
            } else if ("body".equalsIgnoreCase(p.in)) {
                body = p.value;
            } else if ("header".equalsIgnoreCase(p.in)) {
                headers.add(p.name, p.value.toString());
            } else if ("path".equalsIgnoreCase(p.in)) {
                path = path.replace("{" + p.name + "}", p.value.toString());
            }
        }
        if (query.size() > 0) {
            path = path + "?" + StringUtils.join(query, "&");
        }
        try {
            log.debug("class is {}", clazz);
            HttpEntity request = new HttpEntity(body, headers);
            ResponseEntity<List<T>> responseEntity = restTemplate.exchange(
                    gateWayAddress + path,
                    HttpMethod.resolve(httpMethod.toUpperCase()),
                    request,
                    new ParameterizedTypeReference<List<T>>() {}
            );
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getResponseBodyAsString(), e);
        }
    }

    public static class Param {
        public String name;
        public String in;
        public Object value;

        public static Param of(String name, String in, Object value) {
            Param p = new Param();
            p.name = name;
            p.in = in;
            p.value = value;
            return p;
        }
    }
}
