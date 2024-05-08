package com.defaulttenant.hotswap.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TraceIdHolder {
    private TraceIdHolder() {}

    public static final String LOG_TRACE_PREFIX = "==== uuid:";
    public static final String LOG_TRACE_KEY = "logTraceUUID";
    public static final String TRACE_ID = "TraceId";

    public static final ThreadLocal<CacheBean> values = new ThreadLocal<>();

    public static void setTraceId(String uuid) {
        CacheBean cacheBean = initCacheBean();
        cacheBean.setTraceId(uuid);
    }

    public static void setExceptionInfo(String serviceName, String serviceMethod, Object[] args) {
        CacheBean cacheBean = initCacheBean();
        cacheBean.setServiceName(serviceName);
        cacheBean.setServiceMethod(serviceMethod);
        cacheBean.setServiceArgs(args);
    }

    public static String getLogPrefix() {
        if (StringUtils.isEmpty(getTraceId())) {
            return "";
        } else {
            return String.format("[Request %s]", getTraceId());
        }
    }

    public static String getTraceId() {
        if (values.get() == null) {
            return "";
        }
        CacheBean cacheBean = initCacheBean();
        return cacheBean.getTraceId();
    }

    public static String getLogExceptionInfo() {
        CacheBean cacheBean = values.get();

        if (null == cacheBean) {
            return "";
        }

        if (StringUtils.isEmpty(cacheBean.getServiceName())) {
            return "";
        }

        return String.format("%nClass: %s%nMethod: %s%nParams: %s",
                cacheBean.getServiceName(), cacheBean.getServiceMethod(),
                JacksonUtils.toJson(filterArgs(cacheBean.getServiceArgs())));
    }

    public static void remove() {
        values.remove();
    }

    private static CacheBean initCacheBean() {
        if (null == values.get()) {
            values.set(new CacheBean());
        }

        return values.get();
    }

    private static List<Object> filterArgs(Object[] args) {
        if (null == args || args.length == 0) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<>(args.length);

        for (Object arg : args) {
            if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;
                result.add(String.format("MultipartFile;filename=%s;size=%s", file.getOriginalFilename(), file.getSize()));
            } else {
                result.add(arg);
            }
        }

        return result;
    }

    static class CacheBean {
        private String traceId;
        private String serviceName;
        private String serviceMethod;
        private Object[] serviceArgs;

        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceMethod() {
            return serviceMethod;
        }

        public void setServiceMethod(String serviceMethod) {
            this.serviceMethod = serviceMethod;
        }

        public Object[] getServiceArgs() {
            return serviceArgs;
        }

        public void setServiceArgs(Object[] serviceArgs) {
            this.serviceArgs = serviceArgs;
        }
    }
}
