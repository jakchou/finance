package com.defaulttenant.hotswap.web.interceptor;

import com.defaulttenant.hotswap.util.TraceIdHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * logging service param while happen exception and cost time
 *
 * @author sys
 * @date 2022-03-25 15:30
 */
@Aspect
@Component
public class ServiceLoggingAspect {
    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut(
            "within(@org.springframework.stereotype.Repository *)" +
                    " || within(@org.springframework.stereotype.Service *)"
    )
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
     *
     * @param joinPoint join point we want the logger for.
     * @return {@link Logger} associated to the given {@link JoinPoint}.
     */
    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * log service cost time and log exception servicename
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws {@link IllegalArgumentException}.
     */
    @Around("springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = logger(joinPoint);
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            TraceIdHolder.setExceptionInfo(joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), joinPoint.getArgs());
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            if (cost >= 1000) {
                logger.info("{} {} cost {}ms", TraceIdHolder.getLogPrefix(),
                        joinPoint.getSignature().getName(), System.currentTimeMillis() - startTime);
            }
        }
    }

    public List<Object> filterArgs(Object[] args) {
        if (null == args || args.length == 0) {
            return Collections.emptyList();
        }
        List<Object> result = new ArrayList<>(args.length);

        for (Object arg : args) {
            if (arg instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) arg;
                result.add(String.format("MultipartFile;filename=%s;size=%s",
                        file.getOriginalFilename(), file.getSize()));
            } else {
                result.add(arg);
            }
        }

        return result;
    }
}
