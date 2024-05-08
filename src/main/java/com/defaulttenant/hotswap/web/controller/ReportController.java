package com.defaulttenant.hotswap.web.controller;

import com.defaulttenant.hotswap.config.LcpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Get report data
 */
@RestController
public class ReportController {
    private final Logger log = LoggerFactory.getLogger(ReportController.class);
    @Resource
    private LcpProperties lcpProperties;

    private static final String GET_TOKEN_API = "/api/dash/util/genToken";
    private static final String GET_TOKEN_FAILED_MESSAGE = "Get token failed ";

    @GetMapping("/api/system/report")
    public ReportResult getReportConfig(@RequestParam(value = "reportIdList", required = false) List<String> reportIdList,
                                        @RequestParam(value = "componentIdList", required = false) List<String> componentIdList) {
        ReportResult result = new ReportResult();
        LcpProperties.Report report = lcpProperties.getReport();
        try {
            result.setProjectId(report.getProjectId());
            GetTokenResponseDto responseDto = getToken(report.getHost(), report.getTokenKey(), report.getDomainId());
            if (responseDto == null || responseDto.getCode() != 200) {
                result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                result.setMsg(responseDto == null ? GET_TOKEN_FAILED_MESSAGE : GET_TOKEN_FAILED_MESSAGE + responseDto.getMessage());
                return result;
            }
            result.setToken(responseDto.getResult());
        } catch (Exception e) {
            log.error(GET_TOKEN_FAILED_MESSAGE, e);
            result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setMsg(GET_TOKEN_FAILED_MESSAGE);
            return result;
        }

        if (!CollectionUtils.isEmpty(reportIdList)) {
            result.setReportIdMap(new HashMap<>());
            if (CollectionUtils.isEmpty(report.getReportIdMap())) {
                reportIdList.forEach(reportId -> result.getReportIdMap().put(reportId, Long.valueOf(reportId)));
            } else {
                reportIdList.forEach(reportId ->
                        result.getReportIdMap().put(reportId, report.getReportIdMap().get(reportId)));
            }
        }
        if (!CollectionUtils.isEmpty(componentIdList)) {
            result.setComponentIdMap(new HashMap<>());
            if (CollectionUtils.isEmpty(report.getComponentIdMap())) {
                componentIdList.forEach(componentId -> result.getComponentIdMap().put(componentId, componentId));
            } else {
                componentIdList.forEach(componentId ->
                        result.getComponentIdMap().put(componentId, report.getComponentIdMap().get(componentId)));
            }
        }

        return result;
    }

    private GetTokenResponseDto getToken(String host, String tokenKey, Long domainId) {
        String uri = host + GET_TOKEN_API;
        GetTokenRequestDto getTokenRequestDto = new GetTokenRequestDto();
        getTokenRequestDto.setKey(tokenKey);
        getTokenRequestDto.setDomainId(domainId);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<GetTokenRequestDto> body = new HttpEntity<>(getTokenRequestDto);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setRequestFactory(new ProxyController.CloudClientHttpRequestFactory(30000, 5000));
        ResponseEntity<GetTokenResponseDto> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, body, GetTokenResponseDto.class);
        return responseEntity.getBody();
    }

    static class ReportResult {
        private int code = 200;
        private String msg;
        private String token;
        private Long projectId;
        private Map<String, Long> reportIdMap;
        private Map<String, String> componentIdMap;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public Map<String, Long> getReportIdMap() {
            return reportIdMap;
        }

        public void setReportIdMap(Map<String, Long> reportIdMap) {
            this.reportIdMap = reportIdMap;
        }

        public Map<String, String> getComponentIdMap() {
            return componentIdMap;
        }

        public void setComponentIdMap(Map<String, String> componentIdMap) {
            this.componentIdMap = componentIdMap;
        }
    }

    static class GetTokenRequestDto {
        private String tokenType = "tokenKey";
        private String key;
        private Long domainId;

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Long getDomainId() {
            return domainId;
        }

        public void setDomainId(Long domainId) {
            this.domainId = domainId;
        }
    }

    static class GetTokenResponseDto {
        private int code;
        private String result;
        private String message;
        private String logPath;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLogPath() {
            return logPath;
        }

        public void setLogPath(String logPath) {
            this.logPath = logPath;
        }
    }
}
