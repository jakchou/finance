package com.defaulttenant.hotswap.util;

/**
 * lambda表达式变量的封装对象
 * @param <T>
 */
public class LambdaParamWrapper<T> {
    public T param;

    public LambdaParamWrapper() {
    }

    public LambdaParamWrapper(T param) {
        this.param = param;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }
}
