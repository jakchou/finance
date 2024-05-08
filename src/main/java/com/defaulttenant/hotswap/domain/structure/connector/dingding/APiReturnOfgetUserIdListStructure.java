package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class APiReturnOfgetUserIdListStructure {

    public Long errcode;
    public String errmsg;
    public com.defaulttenant.hotswap.domain.structure.connector.dingding.ResultStructure result;
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

    public com.defaulttenant.hotswap.domain.structure.connector.dingding.ResultStructure getResult() {
        return result;
    } 

    public void setResult(com.defaulttenant.hotswap.domain.structure.connector.dingding.ResultStructure result) {
        this.result = result; 
    } 

    public String getRequest_id() {
        return request_id;
    } 

    public void setRequest_id(String request_id) {
        this.request_id = request_id; 
    } 


}
