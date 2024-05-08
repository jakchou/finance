package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class APiReturnOfgetDepartmentStructure {

    public Long errcode;
    public String errmsg;
    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Result6Structure> result = new ArrayList<>();
    public String request_id;
    public Long getErrcode() {
        return errcode;
    } 

    public void setErrcode(Long errcode) {
        this.errcode = errcode; 
    } 

    public String getErrmsg() {
        return errmsg;
    } 

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg; 
    } 

    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Result6Structure> getResult() {
        return result;
    } 

    public void setResult(List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Result6Structure> result) {
        this.result = result; 
    } 

    public String getRequest_id() {
        return request_id;
    } 

    public void setRequest_id(String request_id) {
        this.request_id = request_id; 
    } 


}
