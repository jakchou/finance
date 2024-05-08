package com.defaulttenant.hotswap.web.validation;

/**
 * @author sys
 */
public @interface ValidationRule {
    String value();
    String targetName();
    String targetFunction() default "";
    String argvs() default "";
    String errorMsg() default "";
}
