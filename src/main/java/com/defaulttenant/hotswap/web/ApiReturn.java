package com.defaulttenant.hotswap.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.defaulttenant.hotswap.util.TraceIdHolder;

public class ApiReturn<T> {
    @JsonProperty("RequestId")
    private String requestId;

    @JsonProperty("Code")
    private int code = 200;

    @JsonProperty("Message")
    private String message = "";

    @JsonProperty("StackTrace")
    private String stackTrace = "";

    @JsonProperty("Data")
    private T data;

    public static <T> ApiReturn<T> of(T data) {
        ApiReturn apiReturn = new ApiReturn<>();
        apiReturn.setRequestId(TraceIdHolder.getTraceId());
        apiReturn.setData(data);
        return apiReturn;
    }

    public static <T> ApiReturn<T> of(T data, int code) {
        ApiReturn apiReturn = new ApiReturn<>();
        apiReturn.setRequestId(TraceIdHolder.getTraceId());
        apiReturn.setData(data);
        apiReturn.setCode(code);
        return apiReturn;
    }

    public static <T> ApiReturn<T> of(T data, int code, String message) {
        ApiReturn apiReturn = new ApiReturn<>();
        apiReturn.setRequestId(TraceIdHolder.getTraceId());
        apiReturn.setData(data);
        apiReturn.setCode(code);
        apiReturn.setMessage(message);
        return apiReturn;
    }

    public static <T> ApiReturn<T> of(T data, int code, String message, String stackTrace) {
        ApiReturn apiReturn = new ApiReturn<>();
        apiReturn.setRequestId(TraceIdHolder.getTraceId());
        apiReturn.setData(data);
        apiReturn.setCode(code);
        apiReturn.setMessage(message);
        apiReturn.setStackTrace(stackTrace);
        return apiReturn;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
