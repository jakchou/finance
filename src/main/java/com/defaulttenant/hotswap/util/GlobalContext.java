package com.defaulttenant.hotswap.util;

import com.defaulttenant.hotswap.domain.SessionVariables;
import com.defaulttenant.hotswap.domain.http.HttpRequest;
import com.defaulttenant.hotswap.domain.http.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.compress.utils.Lists;
import java.lang.reflect.ParameterizedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

public class GlobalContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalContext.class);
    public static final String TOKEN = "token";
    public static final String DOMAINNAME = "domainName";
    public static final String TIMEZONE = "timeZone";

    private static final ThreadLocal<Map<String, String>> threadLocal = new InheritableThreadLocal<Map<String, String>>() {

        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    private static ThreadLocal<SessionVariables> sessionVariablesThreadLocal = ThreadLocal.withInitial(() -> new SessionVariables());


    private GlobalContext() {
    }

    /**
    * 验证规则是否处理过
    */
    private static ThreadLocal<Boolean> validationThreadLocal = new ThreadLocal<>();

    public static boolean notHandleValidation(){
        Boolean handleValidation = validationThreadLocal.get();
        return handleValidation == null || !handleValidation;
    }

    public static void setValidation(Boolean validation){
        validationThreadLocal.set(validation);
    }

    public static void clearValidation(){
        validationThreadLocal.remove();
    }

    public static Map<String, String> get() {
        return threadLocal.get();
    }

    public static void set(Map<String, String> data) {
        if (data == null) {
            threadLocal.get();
        } else {
            threadLocal.set(data);
        }
    }

    public static void clear() {
        if (null != threadLocal.get()) {
            threadLocal.remove();
        }
        if (null != sessionVariablesThreadLocal.get()) {
            sessionVariablesThreadLocal.remove();
        }
    }

    public static String getToken() {
        Map<String, String> data = threadLocal.get();
        return data.get(TOKEN);
    }

    public static String getDomainname() {
        Map<String, String> data = threadLocal.get();
        return data.get(DOMAINNAME);
    }

    public static void setToken(String token) {
        threadLocal.get().put(TOKEN, token);
    }

    public static void setDomainName(String domainName) {
        threadLocal.get().put(DOMAINNAME, domainName);
    }

    public static void setGlobalValue(String key, String value) {
        threadLocal.get().put(key, value);
    }

    public static SessionVariables getSessionVariable() {
        return sessionVariablesThreadLocal.get();
    }

    public static void setTimeZone(String timeZone) {
        threadLocal.get().put(TIMEZONE, timeZone);
    }

    public static String getTimeZone() {
        Map<String, String> data = threadLocal.get();
        return data.get(TIMEZONE);
    }

    public static List<HttpResponse> findHttpResponseFieldsInSessionVariables() {
        SessionVariables sessionVariables = getSessionVariable();
        if (Objects.isNull(sessionVariables)) {
            return Lists.newArrayList();
        }
        List<HttpResponse> httpResponses = new ArrayList<>();
        Field[] fields = sessionVariables.getClass().getDeclaredFields();
        List<Field> matchedFields = new ArrayList<>();
        for (Field field : fields) {
            if (field.getType() == HttpResponse.class) {
                matchedFields.add(field);
                field.setAccessible(true);
                try {
                    httpResponses.add((HttpResponse) field.get(sessionVariables));
                } catch (IllegalAccessException e) {
                    LOGGER.error("sessionVariables get httpResponse fail={}", e.getMessage());
                }
            }
        }
        return httpResponses;
    }
    public static void fillInCurrentUserInSessionVariable(String userId,String userName) {
        SessionVariables sessionVariables = getSessionVariable();
        if(Objects.isNull(sessionVariables)){
            sessionVariables = new SessionVariables();
        }
        Field[] fields = sessionVariables.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getType().getName().equals("com.netease.lowcode.auth.domain.LCAPUser")  && "currentUser".equalsIgnoreCase(field.getName())) {
                    Field idField = ReflectionUtils.findField(field.getType(), "userId", String.class);
                    Field nameField = ReflectionUtils.findField(field.getType(), "userName", String.class);
                    if (Objects.nonNull(idField)) {
                        ReflectionUtils.makeAccessible(idField);
                        ReflectionUtils.setField(idField,ReflectionUtils.getField(field,sessionVariables),userId);
                    }
                    if (Objects.nonNull(nameField)) {
                        ReflectionUtils.makeAccessible(nameField);
                        ReflectionUtils.setField(nameField, ReflectionUtils.getField(field,sessionVariables), userName);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("sessionVariables autowire CurrentUser fail={}", e.getMessage());
            }
        }
        sessionVariablesThreadLocal.set(sessionVariables);
    }

    public static void fillInCurrentUserInSessionVariable(Object object) {
        SessionVariables sessionVariables = getSessionVariable();
        if(Objects.isNull(sessionVariables)){
            sessionVariables = new SessionVariables();
        }
        Field[] fields = sessionVariables.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getType().getName().equals("com.netease.lowcode.auth.domain.LCAPUser")  && "currentUser".equalsIgnoreCase(field.getName())) {
                    field.set(sessionVariables, object);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error("sessionVariables autowire CurrentUser fail={}", e.getMessage());
            }
        }
        sessionVariablesThreadLocal.set(sessionVariables);
    }

    public static void initializingSessionVariables(HttpServletRequest request) {
        SessionVariables sessionVariables = getSessionVariable();
        if(Objects.isNull(sessionVariables)){
            sessionVariables = new SessionVariables();
        }
        Field[] fields = sessionVariables.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getType() == HttpRequest.class) {
                    Class requestTClazz = Object.class;
                    if(field.getGenericType() instanceof ParameterizedType){
                        requestTClazz = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                    }
                    field.set(sessionVariables, HttpRequest.createFormHttpServletRequest(request,requestTClazz));
                }
            } catch (Exception e) {
                LOGGER.error("sessionVariables autowire httpRequestAndClientType fail={}", e.getMessage());
            }
        }
        sessionVariablesThreadLocal.set(sessionVariables);
    }
}
