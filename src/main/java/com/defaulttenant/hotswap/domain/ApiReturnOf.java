package com.defaulttenant.hotswap.domain;

/**
 * System built in generic class
 * ApiReturnOf
 *
 * @author sys
 */
public class ApiReturnOf<T> {
    public String Message;

    public T Data;

    public Integer Code;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    @Override
    public String toString() {
        return "ApiReturnOf{" + "Message='" + Message + '\'' + ", Data=" + Data + ", Code=" + Code + '}';
    }
}
