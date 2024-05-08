package com.defaulttenant.hotswap.datasource.dynamic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sys
 */
@Target({ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String value();
}
