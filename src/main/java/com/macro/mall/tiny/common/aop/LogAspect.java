package com.macro.mall.tiny.common.aop;
import com.alibaba.fastjson.JSONObject;
import com.macro.mall.tiny.common.annotation.OperationLogAnnotation;
import com.macro.mall.tiny.domain.AdminUserDetails;
import com.macro.mall.tiny.modules.buss.model.SysOperLog;
import com.macro.mall.tiny.modules.buss.service.SysOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private SysOperLogService sysOperLogService;

    @Pointcut("@annotation(com.macro.mall.tiny.common.annotation.OperationLogAnnotation)")
    public void logPointCut() {
    }


    @AfterReturning(pointcut = "logPointCut()")
    public void doAfterReturning(JoinPoint joinPoint) {
        handleLog(joinPoint, null);
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e);
    }


    private void handleLog(final JoinPoint joinPoint, final Exception e) {
        // 获得OperationLogAnnotation注解
        OperationLogAnnotation controllerLog = getAnnotationLog(joinPoint);
        if (controllerLog == null) {
            return;
        }
        SysOperLog operLog = new SysOperLog();
        // 操作状态（0正常 1异常）
        operLog.setStatus(0);
        operLog.setOperTime(new Date());
        if (e != null) {
            operLog.setStatus(1);
            String errorMsg = e.getMessage().length() > 2000 ? e.getMessage().substring(0, 2000) : e.getMessage();
            operLog.setErrorMsg(errorMsg);
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            AdminUserDetails adminUserDetails = (AdminUserDetails) principal;
            // 操作用户
            operLog.setUserId(adminUserDetails.getUmsAdmin().getId());
            operLog.setUri(adminUserDetails.getPath());
        }
        // 处理注解上的参数
        getControllerMethodDescription(joinPoint, controllerLog, operLog);
        // 保存数据库
        sysOperLogService.save(operLog);
    }




    private void getControllerMethodDescription(JoinPoint joinPoint, OperationLogAnnotation logAnnotation, SysOperLog operLog) {
        // 设置业务类型（0其它 1新增 2修改 3删除）
        operLog.setBusinessType(logAnnotation.businessType().ordinal());
        // 设置模块标题，eg:登录
        operLog.setTitle(logAnnotation.title());
        // 对方法上的参数进行处理，处理完：userName=xxx,password=xxx
        String optParam = getAnnotationValue(joinPoint, logAnnotation.optParam());
        operLog.setOptParam(optParam);

    }



    private String getAnnotationValue(JoinPoint joinPoint, String name) {
        String paramName = name;
        // 获取方法中所有的参数
        Map<String, Object> params = getParams(joinPoint);
        // 参数是否是动态的:#{paramName}
        if (paramName.matches("^#\\{\\D*\\}")) {
            // 获取参数名,去掉#{ }
            paramName = paramName.replace("#{", "").replace("}", "");
            // 是否是复杂的参数类型:对象.参数名
            if (paramName.contains(".")) {
                String[] split = paramName.split("\\.");
                // 获取方法中对象的内容
                Object object = getValue(params, split[0]);
                // 转换为JsonObject
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
                // 获取值
                Object o = jsonObject.get(split[1]);
                return String.valueOf(o);
            } else {// 简单的动态参数直接返回
                StringBuilder str = new StringBuilder();
                String[] paraNames = paramName.split(",");
                for (String paraName : paraNames) {

                    String val = String.valueOf(getValue(params, paraName));
                    // 组装成 userName=xxx,password=xxx,
                    str.append(paraName).append("=").append(val).append(",");
                }
                // 去掉末尾的,
                if (str.toString().endsWith(",")) {
                    String substring = str.substring(0, str.length() - 1);
                    return substring;
                } else {
                    return str.toString();
                }
            }
        }
        // 非动态参数直接返回
        return name;
    }



    public Map<String, Object> getParams(JoinPoint joinPoint) {
        Map<String, Object> params = new HashMap<>(8);
        // 通过切点获取方法所有参数值["zhangsan", "123456"]
        Object[] args = joinPoint.getArgs();
        // 通过切点获取方法所有参数名 eg:["userName", "password"]
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] names = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            params.put(names[i], args[i]);
        }
        return params;
    }



    private Object getValue(Map<String, Object> map, String paramName) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equals(paramName)) {
                return entry.getValue();
            }
        }
        return null;
    }



    private OperationLogAnnotation getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(OperationLogAnnotation.class);
        }
        return null;
    }
}
