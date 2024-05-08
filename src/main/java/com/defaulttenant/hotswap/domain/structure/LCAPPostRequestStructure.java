package com.defaulttenant.hotswap.domain.structure; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.HashMap; 
import java.util.Map; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class LCAPPostRequestStructure {

    public com.defaulttenant.hotswap.domain.http.HttpResponse<String> response = new com.defaulttenant.hotswap.domain.http.HttpResponse<>();
    public String status;
    public Map<String, String> requestInfo = new HashMap<>();
    public com.defaulttenant.hotswap.domain.http.HttpResponse<String> getResponse() {
        return response;
    } 

    public void setResponse(com.defaulttenant.hotswap.domain.http.HttpResponse<String> response) {
        this.response = response; 
    } 

    public String getStatus() {
        return status;
    } 

    public void setStatus(String status) {
        this.status = status; 
    } 

    public Map<String, String> getRequestInfo() {
        return requestInfo;
    } 

    public void setRequestInfo(Map<String, String> requestInfo) {
        this.requestInfo = requestInfo; 
    } 


}
