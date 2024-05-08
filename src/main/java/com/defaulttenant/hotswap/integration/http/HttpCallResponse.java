package com.defaulttenant.hotswap.integration.http;

import com.netease.cloud.RemoteCallResponse;

public class HttpCallResponse<T> implements RemoteCallResponse<T>{
    private T result;
    @Override
    public T getResult() {
        return this.result;
    }

    public HttpCallResponse(){}

    public HttpCallResponse(T result){
        this.result = result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
