package com.defaulttenant.hotswap.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.defaulttenant.hotswap.util.DomainUtil;
import com.defaulttenant.hotswap.util.XmlUtils;
import com.defaulttenant.hotswap.integration.http.HttpComponentsClientRestfulHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import com.defaulttenant.hotswap.config.AppAddrProperties;
import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.config.RemoteUserCenterProperties;
import com.defaulttenant.hotswap.util.ExtendMappingJackson2HttpMessageConverter;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Proxy controller for web.
 * Used to proxy official services and own services.
 *
 * @author ifcc
 */

@Controller
public class ProxyController {
    private static final String HEADER_AUTH = "authorization";
    private static final String BODY_TOKEN = "Token";
    private static final String HEAD_ENV = "Env";

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private final Logger log = LoggerFactory.getLogger(ProxyController.class);

    private static final int INTERNAL_ERROR_CODE = 500;

    private static final String INTERNAL_ERROR_MSG = "InternalServerError";

    @Value("${spring.profiles.active:dev}")
    private String env;

    @Value("${lcp.authCenter.host:UNKNOW}")
    private String officialApiGatewayHost;

    @Value("${lcp.gatewayUrl:UNKNOW}")
    private String ownGatewayAddress;

    @Value("${lcp.gatewayType:qingzhou}")
    private String gatewayType;

    private ObjectMapper mapper = new ObjectMapper();

    @Resource
    private AppAddrProperties appAddr;

    @Resource
    private RemoteUserCenterProperties remoteUserCenterConfig;

    @RequestMapping(value = "/gateway/{service}/**")
    public void passThroughFile(
            @RequestBody(required = false) String body,
            @RequestParam(required = false) MultipartFile file,
            @PathVariable("service") String service,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ProxyResult result;
        ProxyServiceEnum proxyServiceEnum = ProxyServiceEnum.getByCode(service);
        if (proxyServiceEnum == null) {
            result = new ProxyResult().setCode(500).setMsg(String.format("The service[%s] is not legal.", service));
            passThroughResponse(result, response);
            return;
        }
        try {
            Object bodyValue = null;
            if (file != null) {
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("file", new MultipartFileResource(file));
                Map<String, String[]> parameterMap = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    if (Constants.UPLOAD_PARAMETER_PATH.equalsIgnoreCase(entry.getKey())) {
                        params.add(entry.getKey(), request.getParameter(entry.getKey()));
                    } else {
                        params.add(entry.getKey(), entry.getValue());
                    }
                }
                bodyValue = params;
            }
            Map<String, String> extraHeaderMap = new HashMap<>();
            extraHeaderMap.put("host", officialApiGatewayHost);
            extraHeaderMap.put("x-forwarded-proto", "");
            extraHeaderMap.put("x-forwarded-host", "");
            extraHeaderMap.put("x-forwarded-port", "");
            extraHeaderMap.put("x-forwarded-for", "");
            extraHeaderMap.put("Accept-Encoding", "");
            bodyValue = bodyValue == null ? body : bodyValue;
            result = passThrough(proxyServiceEnum, request, bodyValue, extraHeaderMap);
        } catch (IOException e) {
            log.error("Proxy service error", e);
            result = new ProxyResult().setCode(500).setMsg(e.getMessage());
        }
        passThroughResponse(result, response);
    }

    @RequestMapping("/gw/**")
    public void gwPassThrough(
            @RequestBody(required = false) String body,
            @RequestParam(value = "file", required = false) List<MultipartFile> files,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ProxyResult result;
        try {
            Object bodyValue = null;
            if (files != null && files.size() > 0) {
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                for (MultipartFile file : files) {
                    params.add(file.getName(), new MultipartFileResource(file));
                }
                Map<String, String[]> parameterMap = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    params.add(entry.getKey(), entry.getValue());
                }
                bodyValue = params;
            }
            Map<String, String> extraHeaderMap = new HashMap<>();
            String host = DomainUtil.getHostFromUrl(ownGatewayAddress);
            extraHeaderMap.put("host", host);
            extraHeaderMap.put("x-forwarded-proto", "");
            extraHeaderMap.put("x-forwarded-host", "");
            extraHeaderMap.put("x-forwarded-port", "");
            extraHeaderMap.put("x-forwarded-for", "");
            extraHeaderMap.put("Accept-Encoding", "");
            bodyValue = bodyValue == null ? body : bodyValue;
            bodyValue = buildFormDataBody(request, bodyValue);
            result = passThrough(ProxyServiceEnum.OWN_SERVICE, request, bodyValue, extraHeaderMap);
        } catch (IOException e) {
            log.error("Proxy service error", e);
            result = new ProxyResult().setCode(500).setMsg(e.getMessage());
        }
        passThroughResponse(result, response);
    }

    private ProxyResult nuimsProxy(ProxyServiceEnum service, HttpServletRequest request, Object body, Map<String, String> extraHeaderMap) throws URISyntaxException {
        URI uri;
        HttpMethod httpMethod;
        HttpHeaders httpHeaders;
        httpMethod = HttpMethod.valueOf(request.getMethod());
        httpHeaders = getRequestHeaders(request);
        httpHeaders.add(HEAD_ENV, env);
        if (!CollectionUtils.isEmpty(extraHeaderMap)) {
            extraHeaderMap.forEach(
                    (k, v) -> {
                        httpHeaders.remove(k.toLowerCase());
                        if (v != null && !"".equals(v.trim())) {
                            httpHeaders.add(k.toLowerCase(), v);
                        }
                    }
            );
        }
        if (Constants.LIGHT_GW_TYPE.equals(gatewayType)) {
            uri = getURI(request);
        } else {
            uri = getURI(service, request);
        }
        return commonPassThrough(uri, httpMethod, httpHeaders, body);
    }

    private ProxyResult passThrough(ProxyServiceEnum service, HttpServletRequest request, Object body, Map<String, String> extraHeaderMap) {
        try {
            return nuimsProxy(service, request, body, extraHeaderMap);
        } catch (URISyntaxException e) {
            log.error("passThrough error", e);
            return new ProxyResult().setCode(INTERNAL_ERROR_CODE).setMsg(INTERNAL_ERROR_MSG);
        }
    }

    private static boolean apiNeedProxyToRemote(HttpServletRequest request){
        List<String> apiNeedProxyInGenerator = new ArrayList<>();
        apiNeedProxyInGenerator.add("Action=GetUser");

        String queryString = request.getQueryString();
        if (StringUtils.isEmpty(queryString)) {
            return false;
        }
        for (String api : apiNeedProxyInGenerator) {
            if (queryString.contains(api)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取远程用户中心的地址
     *
     * @return
     */
    private String getQueryDomain(){
        // 配置的远程用户中心地址
        return remoteUserCenterConfig.getAddress();
    }

    /**
     * 获取远程用户中心的访问路径
     *
     * @return
     */
    private String getQueryPath(){
        return "/nuims";
    }

    /**
     * 获取调用远程用户中心时请求头信息
     * @return
     */
    private HttpHeaders getQueryHead(){
        HttpHeaders headers = new HttpHeaders();
        // 鉴权的接口
        headers.add(HEADER_CONTENT_TYPE, "application/json");
        return headers;
    }

    private String getQueryParam(){
        // 目前制品应用只有需要去远程用户中心鉴权的接口
        // token鉴权的接口(也是获取当前登录用户id和名称的接口)
        return "?Action=Authentication&Version=2020-06-01";
    }

    private URI getURI(ProxyServiceEnum service, HttpServletRequest request) throws URISyntaxException {
        String serviceDomain = getServiceDomain(service);
        String path = request.getRequestURI();
        if (request.getQueryString() != null && !"".equals(request.getQueryString())) {
            return new URI(serviceDomain + path + "?" + request.getQueryString());
        }
        return new URI(serviceDomain + path);
    }

    private URI getURI(HttpServletRequest request) throws URISyntaxException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        path = path.substring(contextPath.length());
        boolean endWithTag = path.endsWith("/");
        String[] splits = path.split("/");
        if (splits.length > 2) {
            // 获取path路径中appName
            String appName = splits[2];
            String serviceDomain = appAddr.getAddr().get(appName);
            path = contextPath + "/" + String.join("/", Arrays.copyOfRange(splits, 3, splits.length));
            if (endWithTag) {
                path = path + "/";
            }
            if (!DomainUtil.matchSupportHttpProtocolPrefix(serviceDomain)) {
                //默认拼接http
                serviceDomain = DomainUtil.SCHEME_HTTP_PREFIX + serviceDomain;
            }
            if (request.getQueryString() != null && !"".equals(request.getQueryString())) {
                return new URI(serviceDomain + path + "?" + request.getQueryString());
            }
            return new URI(serviceDomain + path);
        } else {
            log.error("light gateway path is invalid ：{}", path);
            return new URI(DomainUtil.SCHEME_HTTP_PREFIX + path);
        }
    }



    private String getServiceDomain(ProxyServiceEnum service) {
        if (service == null) {
            return "";
        }
        if (ProxyServiceEnum.AUTH_SERVICE == service) {
            return DomainUtil.SCHEME_HTTP_PREFIX + officialApiGatewayHost;
        }
        if (ProxyServiceEnum.BPMS_SERVICE == service) {
            return DomainUtil.SCHEME_HTTP_PREFIX + officialApiGatewayHost;
        }
        if (ProxyServiceEnum.LOW_CODE_SERVICE == service) {
            return DomainUtil.SCHEME_HTTP_PREFIX + officialApiGatewayHost;
        }
        if (ProxyServiceEnum.OWN_SERVICE == service) {
            return ownGatewayAddress;
        }
        return "";
    }

    private HttpHeaders getRequestHeaders(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            httpHeaders.add(key, value);
        }
        return httpHeaders;
    }

    private ProxyResult commonPassThrough(URI uri, HttpMethod method, HttpHeaders httpHeaders, Object body) {
        log.info("============================ Pass Through Http Log Start ============================");
        log.info("<PassThrough> ==URL== {}", uri);
        log.info("<PassThrough> ==METHOD== {}", method.name());
        log.info("<PassThrough> ==HEADERS== {}", httpHeaders);
        log.info("<PassThrough> ==requestBody== {}", body);

        int code;
        String msg = "";
        byte[] responseBody = null;
        HttpHeaders responseHeaders = null;
        // is xml
        boolean xmlType = XmlUtils.isXmlType(httpHeaders.getContentType());
        RestTemplate restTemplate = new RestTemplate();
        try {
            if (xmlType && Objects.nonNull(body)) {
                body = XmlUtils.jsonToXml(body.toString(), XmlUtils.getXmlCharset(httpHeaders.getContentType()));
            }
            HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            restTemplate.getMessageConverters().add(new ExtendMappingJackson2HttpMessageConverter());
            restTemplate.setRequestFactory(new CloudClientHttpRequestFactory(Constants.HTTP_TIMEOUT_READ_MILLIS, Constants.HTTP_TIMEOUT_CONNECT_MILLIS));
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri, method, entity, byte[].class);

            code = responseEntity.getStatusCode().value();
            responseBody = responseEntity.getBody();
            responseHeaders = responseEntity.getHeaders();
            if (xmlType && responseBody != null) {
                responseBody = XmlUtils.xmlToJson(new String(responseBody)).getBytes();
            }
            log.info("<PassThrough> ==ReturnCode== {}", code);
            log.info("<PassThrough> ==ReturnBody== {}", responseBody == null ? null : new String(responseBody, StandardCharsets.UTF_8));
            log.info("<PassThrough> ==ReturnHeader {}", responseHeaders);
            log.info("============================ Pass Through Http Log End ============================");
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("uri [" + uri + "] invoke HttpClient|ServerErrorException error", ex);
            code = ex.getStatusCode().value();
            msg = ex.getResponseBodyAsString();
            if (msg == null || "".equals(msg.trim())) {
                code = INTERNAL_ERROR_CODE;
                msg = ex.getMessage();
            } else {
                responseBody = ex.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception ex) {
            log.error("uri [" + uri + "] invoke Exception error", ex);
            code = INTERNAL_ERROR_CODE;
            msg = ex.getMessage();
        }
        return new ProxyResult().setCode(code).setMsg(msg).setBody(responseBody).setHttpHeaders(responseHeaders);
    }

    private void passThroughResponse(ProxyResult result, HttpServletResponse response) {
        try {
            response.setStatus(result.getCode());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MappingJackson2JsonView.DEFAULT_CONTENT_TYPE);
            HttpHeaders headers = result.getHttpHeaders();
            if (headers != null) {
                handleAuthHeader(headers, response);
                handleContentType(headers, response);
                handleDisposition(headers, response);
            }
            response.getOutputStream().write(getResponseBody(result));
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleAuthHeader(HttpHeaders headers, HttpServletResponse response) {
        if (headers.containsKey(HEADER_AUTH)) {
            List<String> authHeaders = headers.get(HEADER_AUTH);
            if (authHeaders != null) {
                for (String key : authHeaders) {
                    response.addHeader(HEADER_AUTH, key);
                }
            }
        }
    }

    private void handleContentType(HttpHeaders headers, HttpServletResponse response) {
        if (headers.containsKey(HEADER_CONTENT_TYPE)) {
            List<String> contentTypeHeaders = headers.get(HEADER_CONTENT_TYPE);
            if (contentTypeHeaders != null) {
                for (String key : contentTypeHeaders) {
                    response.addHeader(HEADER_CONTENT_TYPE, key);
                }
            }
        }
    }

    private void handleDisposition(HttpHeaders headers, HttpServletResponse response) {
        if (headers.containsKey(HEADER_CONTENT_TYPE)) {
            List<String> contentTypeHeaders = headers.get(HEADER_CONTENT_TYPE);
            if (contentTypeHeaders != null) {
                for (String key : contentTypeHeaders) {
                    response.addHeader(HEADER_CONTENT_TYPE, key);
                }
            }
        }
    }

    private byte[] getResponseBody(ProxyResult result) throws JsonProcessingException {
        if (result.getCode() == INTERNAL_ERROR_CODE) {
            Map<String, Object> map = new HashMap<>();
            map.put("Code", INTERNAL_ERROR_MSG);
            map.put("Message", result.getMsg());
            return mapper.writeValueAsString(map).getBytes(StandardCharsets.UTF_8);
        }
        // 第三方接口的返回的body可能为null，需要初始化
        if (result.getBody() == null) {
            return new byte[]{};
        }
        return result.getBody();
    }

    static class CloudClientHttpRequestFactory extends HttpComponentsClientRestfulHttpRequestFactory {

        CloudClientHttpRequestFactory(int readTimeout, int connectTimeout) {
            super.setReadTimeout(readTimeout);
            super.setConnectTimeout(connectTimeout);
        }

        @Override
        public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
            return super.createRequest(uri, httpMethod);
        }
    }

    static class ProxyResult {
        private int code;
        private String msg;
        private byte[] body;
        private HttpHeaders httpHeaders;

        public int getCode() {
            return code;
        }

        public ProxyResult setCode(int code) {
            this.code = code;
            return this;
        }

        public String getMsg() {
            return msg;
        }

        public ProxyResult setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public byte[] getBody() {
            return body;
        }

        public ProxyResult setBody(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpHeaders getHttpHeaders() {
            return httpHeaders;
        }

        public ProxyResult setHttpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }
    }

    private static class MultipartFileResource extends ByteArrayResource {
        private String filename;

        private MultipartFileResource(MultipartFile multipartFile) throws IOException {
            super(multipartFile.getBytes());
            this.filename = multipartFile.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }

    private enum ProxyServiceEnum {
        /**
         * 统一认证与权限，属于官方服务
         */
        AUTH_SERVICE("nuims", "统一认证与权限服务"),
        /**
         * 流程引擎服务, 属于官方服务
         */
        BPMS_SERVICE("bpms", "流程引擎服务"),
        /**
         * 低代码平台后端服务, 属于官方服务
         */
        LOW_CODE_SERVICE("lowcode", "低代码平台后端服务"),
        /**
         * 本租户下的服务
         */
        OWN_SERVICE("own", "本租户的服务");

        private String code;
        private String desc;

        ProxyServiceEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static ProxyServiceEnum getByCode(String code) {
            if (code == null) {
                return null;
            }
            return Arrays.stream(values()).filter(it -> it.getCode().equalsIgnoreCase(code)).findAny().orElse(null);
        }
    }

    private Object buildFormDataBody(HttpServletRequest request, Object body) {
        if(body != null){
            return body;
        }
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap != null && parameterMap.size() > 0) {
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    params.add(entry.getKey(), entry.getValue());
                }
                return params;
            }
        }
        return null;
    }
}
