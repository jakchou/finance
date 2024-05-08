package com.defaulttenant.hotswap.web.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.defaulttenant.hotswap.domain.enumeration.ErrorCodeEnum;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.util.SqlKeywordsUtils;
import com.defaulttenant.hotswap.web.interceptor.annotation.SqlInjectionCheck;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * sql injection check
 */
@Component
@Aspect
public class SqlInjectionCheckAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlInjectionCheckAspect.class);

    @Pointcut(value = "@annotation(com.defaulttenant.hotswap.web.interceptor.annotation.SqlInjectionCheck)")
    public void pointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around(value = "pointcut()")
    public Object sqlInjectionCheckAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SqlInjectionCheck annotation = method.getAnnotation(SqlInjectionCheck.class);
        if (null != annotation && null != annotation.paramIndexList() && annotation.paramIndexList().length > 0) {
            Object[] args = joinPoint.getArgs();
            String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
            Map<String, Integer> paramNameMap = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                paramNameMap.put(paramNames[i], i);
            }
            List<String> checkValueList = new ArrayList<>();
            for (String paramIndex : annotation.paramIndexList()) {
                String checkValue = handleParam(paramIndex, paramNameMap, args);
                if (StringUtils.isNotBlank(checkValue)) {
                    checkValueList.add(checkValue);
                }
            }
            Set<String> checkResult = SqlKeywordsUtils.getForbiddenKeyWords(annotation.databaseType(), checkValueList);
            if (!checkResult.isEmpty()) {
                LOGGER.info("sqlInjection check fail, invalid keywords {}", checkResult);
                throw new HttpCodeException(400, ErrorCodeEnum.PARAM_INVALID.code, checkResult);
            }
        }
        return joinPoint.proceed();
    }

    private String handleParam(String paramIndex, Map<String, Integer> paramNameMap, Object[] args) {
        String[] paramIndexSplit = paramIndex.split("\\.");
        if (paramNameMap.get(paramIndexSplit[0]) == null
                || args[paramNameMap.get(paramIndexSplit[0])] == null) {
            return null;
        }
        Object paramValue = args[paramNameMap.get(paramIndexSplit[0])];
        if (paramIndexSplit.length == 1) {
            if (String.class.equals(paramValue.getClass())) {
                return paramValue.toString();
            }
            return null;
        }

        return getCheckValue(paramValue, paramIndexSplit);
    }

    private String getCheckValue(Object paramValue, String[] paramIndexSplit) {
        for (int i = 1; i < paramIndexSplit.length; i++) {
            try {
                Field ext = paramValue.getClass().getDeclaredField(paramIndexSplit[i]);
                paramValue = ext.get(paramValue);
                if (paramValue == null) {
                    return null;
                }
                if (String.class.equals(paramValue.getClass())) {
                    return paramValue.toString();
                }
            } catch (Exception ignored) {
                break;
            }
        }
        return null;
    }
}
