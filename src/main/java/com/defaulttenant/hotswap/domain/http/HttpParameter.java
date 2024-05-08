package com.defaulttenant.hotswap.domain.http;


/**
 * http-query-string
 * @param <T>
 */
public class HttpParameter<T> {
    private String name;
    private T value;

    public String getName() {
        return name;
    }

    public HttpParameter<T> setName(String name) {
        this.name = name;
        return this;
    }

    public T getValue() {
        return value;
    }

    public HttpParameter<T> setValue(T value) {
        this.value = value;
        return this;
    }
}
