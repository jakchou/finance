package com.macro.mall.tiny.common.annotation;

import com.macro.mall.tiny.common.constant.BusinessType;

import java.lang.annotation.*;

/**
 * @title: OperationLog
 * @description: 自定义操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogAnnotation {
    String title() default ""; // 操作模块
    BusinessType businessType() default BusinessType.OTHER;
    String optParam() default "";

}