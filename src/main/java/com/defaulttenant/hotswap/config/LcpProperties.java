package com.defaulttenant.hotswap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.defaulttenant.hotswap.util.SnowflakeIdWorker;

import java.util.List;
import java.util.Map;

/**
 * lcp properties
 *
 * @author sys
 * @date 2021-08-05 11:23
 */
@ConfigurationProperties(prefix = "lcp", ignoreInvalidFields=true)
public class LcpProperties {
    private AuthCenter authCenter;
    private String flowUrl;
    private String gatewayUrl;
    private Audit audit;
    private String uiResourceAddress;
    private Upload upload;
    @Value("spring.application.name:lcap")
    private String appName;
    private Boolean returnExceptionStack = false;
    private Boolean logResponse = false;
    private Boolean logRequest = false;
    private Long snowflakeNodeId;
    private Map<String, String> quartzTables;
    private Report report;
    private List<String> noAuthPaths;
    private String nuims;
    private Map<String, FrontendDTO> frontends;
    private Wechat wechat;


    public AuthCenter getAuthCenter() {
        return authCenter;
    }

    public void setAuthCenter(AuthCenter authCenter) {
        this.authCenter = authCenter;
    }

    public String getFlowUrl() {
        return flowUrl;
    }

    public void setFlowUrl(String flowUrl) {
        this.flowUrl = flowUrl;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public String getUiResourceAddress() {
        return uiResourceAddress;
    }

    public void setUiResourceAddress(String uiResourceAddress) {
        this.uiResourceAddress = uiResourceAddress;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Boolean getReturnExceptionStack() {
        return returnExceptionStack;
    }

    public void setReturnExceptionStack(Boolean returnExceptionStack) {
        this.returnExceptionStack = returnExceptionStack;
    }

    public Boolean getLogResponse() {
        return logResponse;
    }

    public void setLogResponse(Boolean logResponse) {
        this.logResponse = logResponse;
    }

    public Boolean getLogRequest() {
        return logRequest;
    }

    public void setLogRequest(Boolean logRequest) {
        this.logRequest = logRequest;
    }

    public Long getSnowflakeNodeId() {
        return snowflakeNodeId;
    }

    public void setSnowflakeNodeId(Long snowflakeNodeId) {
        this.snowflakeNodeId = snowflakeNodeId;
        SnowflakeIdWorker.setDefaultServiceSequence(snowflakeNodeId);
    }

    public Map<String, String> getQuartzTables() {
        return quartzTables;
    }

    public void setQuartzTables(Map<String, String> quartzTables) {
        this.quartzTables = quartzTables;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public List<String> getNoAuthPaths() {
        return noAuthPaths;
    }

    public void setNoAuthPaths(List<String> noAuthPaths) {
        this.noAuthPaths = noAuthPaths;
    }

    public String getNuims() {
        return nuims;
    }

    public void setNuims(String nuims) {
        this.nuims = nuims;
    }

    public Map<String, FrontendDTO> getFrontends() {
        return frontends;
    }

    public void setFrontends(Map<String, FrontendDTO> frontends) {
        this.frontends = frontends;
    }

    public Wechat getWechat() {
        return wechat;
    }

    public void setWechat(Wechat wechat) {
        this.wechat = wechat;
    }

    public static class AuthCenter {
        private Boolean enable;
        private String address;
        private String host;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
    }

    public static class Audit {
        private Boolean enable;
        private String address;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class Upload {
        private String sinkType;
        private String sinkPath;
        private String s3Address;
        private String s3AccessKey;
        private String s3SecretKey;
        private String s3Bucket;
        private String access = "public";
        private Double ttl = -1D;
        private String cdnAddress;

        public String getCdnAddress() {
            return cdnAddress;
        }

        public void setCdnAddress(String cdnAddress) {
            this.cdnAddress = cdnAddress;
        }

        public String getSinkType() {
            return sinkType;
        }

        public void setSinkType(String sinkType) {
            this.sinkType = sinkType;
        }

        public String getSinkPath() {
            return sinkPath;
        }

        public void setSinkPath(String sinkPath) {
            this.sinkPath = sinkPath;
        }

        public String getS3Address() {
            return s3Address;
        }

        public void setS3Address(String s3Address) {
            this.s3Address = s3Address;
        }

        public String getS3AccessKey() {
            return s3AccessKey;
        }

        public void setS3AccessKey(String s3AccessKey) {
            this.s3AccessKey = s3AccessKey;
        }

        public String getS3SecretKey() {
            return s3SecretKey;
        }

        public void setS3SecretKey(String s3SecretKey) {
            this.s3SecretKey = s3SecretKey;
        }

        public String getS3Bucket() {
            return s3Bucket;
        }

        public void setS3Bucket(String s3Bucket) {
            this.s3Bucket = s3Bucket;
        }

        public String getAccess() {
            return access;
        }

        public void setAccess(String access) {
            this.access = access;
        }

        public Double getTtl() {
            return ttl;
        }

        public void setTtl(Double ttl) {
            this.ttl = ttl;
        }
    }

    public static class Report {
        private String host;
        private String tokenKey;
        private Long domainId;
        private Long projectId;
        private Map<String, Long> reportIdMap;
        private Map<String, String> componentIdMap;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getTokenKey() {
            return tokenKey;
        }

        public void setTokenKey(String tokenKey) {
            this.tokenKey = tokenKey;
        }

        public Long getDomainId() {
            return domainId;
        }

        public void setDomainId(Long domainId) {
            this.domainId = domainId;
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

    public static class Wechat {
        private Map<String, String> pageTitle;
        private Map<String, String> secret;
        private Map<String, String> appId;

        public Map<String, String> getAppId() {
            return appId;
        }

        public void setAppId(Map<String, String> appId) {
            this.appId = appId;
        }

        public Map<String, String> getPageTitle() {
            return pageTitle;
        }

        public void setPageTitle(Map<String, String> pageTitle) {
            this.pageTitle = pageTitle;
        }

        public Map<String, String> getSecret() {
            return secret;
        }

        public void setSecret(Map<String, String> secret) {
            this.secret = secret;
        }
    }

    public static class FrontendDTO {
        // 端名称
        private String title;
        // 端标识
        private String name;
        // 端类型
        private String type;
        // 端路径
        private String path;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
