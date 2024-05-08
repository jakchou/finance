package com.defaulttenant.hotswap.web.validation;

/**
 * 一个接口可能被多个来源调用，不同调用来源可能有不同的验证规则，所以需要对验证规则分组
 * @author sys
 */
public @interface ValidationRuleGroup {
    String value();
    ValidationRule[] rules() default {};
}
