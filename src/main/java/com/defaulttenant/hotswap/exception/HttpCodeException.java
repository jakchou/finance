package com.defaulttenant.hotswap.exception;

import org.springframework.http.HttpStatus;

/**
 * 带上http响应码的异常，在返回给客户端时设置响应
 *
 * @author sys
 * @date 2021-07-26 23:03
 */
public class HttpCodeException extends RuntimeException {
    private int httpCode;
    private String errorKey;
    private transient Object[] args;
    private String message;

    public HttpCodeException(String errorKey) {
        this.httpCode = 500;
        this.errorKey = errorKey;
    }

    public HttpCodeException(int httpCode, String errorKey) {
        this.httpCode = httpCode;
        this.errorKey = errorKey;
    }

    public HttpCodeException(int httpCode, String errorKey, Object... args) {
        this.httpCode = httpCode;
        this.errorKey = errorKey;
        this.args = args;
    }

    public HttpCodeException(int httpCode, Throwable t) {
        super(t);
        this.httpCode = httpCode;
        this.errorKey = t.getMessage();
    }

    public HttpCodeException(String errorKey, Throwable t) {
        super(t);
        this.httpCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.errorKey = errorKey;
    }

    public HttpCodeException(int httpCode, String errorKey, Throwable t) {
        super(t);
        this.httpCode = httpCode;
        this.errorKey = errorKey;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {return this.message;}
}
