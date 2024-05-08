package com.defaulttenant.hotswap.web.interceptor;

import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.util.*;
import com.defaulttenant.hotswap.web.validation.Validation;
import com.defaulttenant.hotswap.web.validation.ValidationRule;
import com.defaulttenant.hotswap.web.validation.ValidationRuleGroup;
import com.defaulttenant.hotswap.web.validation.Validator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class ValidationAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationAspect.class);
    private static final String CALL_LOGIC_UUID_HEADER = "lcap-calllogic-uuid";
    private static final Map<String, List<Validator>> validatorCache = new ConcurrentHashMap<>();

    @Pointcut(
            "@annotation(com.defaulttenant.hotswap.web.validation.Validation)"
    )
    public void validatePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around("validatePointcut()")
    public Object validateAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        if (null == methodSignature.getParameterNames() || methodSignature.getParameterNames().length == 0) {
            LOGGER.info("no parameter need to be validated for method: {}",
                    methodSignature.getDeclaringType() + "." + methodSignature.getMethod().getName() + "()");
            return joinPoint.proceed();
        }

        Validation validation = methodSignature.getMethod().getDeclaredAnnotation(Validation.class);
        ValidationRuleGroup[] validationRuleGroups = validation.value();

        if (null == validationRuleGroups || validationRuleGroups.length == 0) {
            return joinPoint.proceed();
        }

        String callLogicUuid = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getHeader(CALL_LOGIC_UUID_HEADER);

        ValidationRuleGroup matchedValidationRuleGroup = searchMatchRuleGroup(validationRuleGroups, callLogicUuid);

        if (null == matchedValidationRuleGroup) {
            throw new HttpCodeException(400, "invalid request");
        }

        Map<String, Object> params = new HashMap<>(joinPoint.getArgs().length);
        for (int i = 0; i < methodSignature.getParameterNames().length; i++) {
            params.put(methodSignature.getParameterNames()[i], joinPoint.getArgs()[i]);
        }

        // 处理默认值，执行完，清除 threadlocal
        try {
            Object[] withDefaultValueParams = null;
            // 参数数量大于0，才处理默认值
            if (joinPoint.getArgs().length > 0) {
                String path = getPath(methodSignature);
                // 开放接口对应的Controller
                boolean restController = path.startsWith("/rest");
                // 逻辑对应的Controller
                boolean logicController = path.startsWith("/api");
                // 目前只有这两类Controller可以设置规则校验
                if (logicController || restController) {
                    Class<?> controllerClass = methodSignature.getDeclaringType();
                    Field[] declaredFields = controllerClass.getDeclaredFields();
                    Field serviceField = null;
                    for (Field declaredField : declaredFields) {
                        if (declaredField.getName().endsWith("CustomizeService")) {
                            serviceField = declaredField;
                            break;
                        }
                    }
                    if (serviceField != null) {
                        ReflectionUtils.makeAccessible(serviceField);
                        Class<?> serviceClass = serviceField.getType();
                        Object serviceInstance = serviceField.get(joinPoint.getTarget());
                        Method[] serviceMethods = serviceClass.getDeclaredMethods();
                        // 获取处理默认值的方法
                        Method handleDefaultValueMethod = null;
                        for (Method method : serviceMethods) {
                            // 开放接口
                            if (restController && method.getName().endsWith("ValidateRestDefaultValue")) {
                                handleDefaultValueMethod = method;
                                break;
                            } else if (!restController && method.getName().endsWith("ValidateDefaultValue")) {
                                // 逻辑接口
                                handleDefaultValueMethod = method;
                                break;
                            }
                        }
                        if (handleDefaultValueMethod != null) {
                            GlobalContext.setValidation(true);
                            // 获取开放接口默认值
                            if (restController) {
                                Object invoked = handleDefaultValueMethod.invoke(serviceInstance, joinPoint.getArgs());
                                if (invoked instanceof Map) {
                                    // 处理默认值一定返回 Map<String, Object>
                                    Map<String, Object> defaultValues = (Map<String, Object>) invoked;
                                    if (!defaultValues.isEmpty()) {
                                        params.putAll(defaultValues);
                                        withDefaultValueParams = defaultValues.values().toArray();
                                    }
                                }
                            } else {
                                // 获取逻辑接口默认值
                                Object invoked = handleDefaultValueMethod.invoke(serviceInstance, joinPoint.getArgs()[0]);
                                params.put(methodSignature.getParameterNames()[0], invoked);
                            }
                        }
                    }
                }
            }

            List<Validator> validators = validatorCache.get(matchedValidationRuleGroup.value());
            if (null != validators) {
                for (Validator validator : validators) {
                    if (!validator.validate(params)) {
                        throw new HttpCodeException(400, validator.errorMsg(params));
                    }
                }
            } else {
                validateAndCache(matchedValidationRuleGroup, params);
            }
            if (withDefaultValueParams != null && withDefaultValueParams.length > 0) {
                return joinPoint.proceed(withDefaultValueParams);
            } else {
                return joinPoint.proceed();
            }
        } finally {
            GlobalContext.clearValidation();
        }
    }

    private String getPath(MethodSignature methodSignature) {
        String path = "";
        Method controllerMethod = methodSignature.getMethod();
        // Controller 中requestMapping一定不为空
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(controllerMethod, RequestMapping.class);
        if (requestMapping == null) {
            return path;
        }
        String[] requestPath = requestMapping.path();
        // 说明直接使用的RequestMapping,目前系统使用的是GetMapping、PostMapping等，为后续做兼容
        if (requestPath.length > 0) {
            path = requestPath[0];
            return path;
        }
        // 处理 GetMapping、PostMapping等
        RequestMethod[] requestMethods = requestMapping.method();
        if (requestMethods.length == 0) {
            return path;
        }
        RequestMethod requestMethod = requestMethods[0];
        switch (requestMethod) {
            case GET:
                GetMapping getMapping = AnnotationUtils.findAnnotation(controllerMethod, GetMapping.class);
                if (getMapping == null || getMapping.path().length == 0) {
                    return path;
                }
                path = getMapping.path()[0];
                break;
            case POST:
                PostMapping postMapping = AnnotationUtils.findAnnotation(controllerMethod, PostMapping.class);
                if (postMapping == null || postMapping.path().length == 0) {
                    return path;
                }
                path = postMapping.path()[0];
                break;
            case PUT:
                PutMapping putMapping = AnnotationUtils.findAnnotation(controllerMethod, PutMapping.class);
                if (putMapping == null || putMapping.path().length == 0) {
                    return path;
                }
                path = putMapping.path()[0];
                break;
            case DELETE:
                DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(controllerMethod, DeleteMapping.class);
                if (deleteMapping == null || deleteMapping.path().length == 0) {
                    return path;
                }
                path = deleteMapping.path()[0];
                break;
            case PATCH:
                PatchMapping patchMapping = AnnotationUtils.findAnnotation(controllerMethod, PatchMapping.class);
                if (patchMapping == null || patchMapping.path().length == 0) {
                    return path;
                }
                path = patchMapping.path()[0];
                break;
            default:
                break;
        }
        return path;
    }

    private ValidationRuleGroup searchMatchRuleGroup(ValidationRuleGroup[] validationRuleGroups, String callLogicUuid) {
        for (ValidationRuleGroup validationRuleGroup : validationRuleGroups) {
            String requireUuid = validationRuleGroup.value();
            if (StringUtils.isEmpty(requireUuid) || requireUuid.equals(callLogicUuid)) {
                // requireUuid empty for rest api
                return validationRuleGroup;
            }
        }

        return null;
    }

    private void validateAndCache(ValidationRuleGroup matchedValidationRuleGroup, Map<String, Object> params) {
        List<Validator> validators = new ArrayList<>(matchedValidationRuleGroup.rules().length);
        for (ValidationRule validationRule : matchedValidationRuleGroup.rules()) {
            Map<String, Object> argvMap = Collections.EMPTY_MAP;
            if (!StringUtils.isEmpty(validationRule.argvs())) {
                argvMap = JacksonUtils.fromJson(validationRule.argvs(), Map.class);
            }

            Validator validator = new Validator.ValidationRuleBuilder(validationRule.value())
                    .withTarget(validationRule.targetName())
                    .withArgument(argvMap)
                    .withErrorMsg(validationRule.errorMsg())
                    .build();
            validators.add(validator);

            boolean isSuccess = true;
            try {
                isSuccess = validator.validate(params);
            } catch (Exception e) {
                LOGGER.error("validate error", e);
                throw new HttpCodeException(400, validator.errorMsg(params));
            }

            if (!isSuccess) {
                throw new HttpCodeException(400, validator.errorMsg(params));
            }
        }

        validatorCache.putIfAbsent(matchedValidationRuleGroup.value(), validators);
    }
}
