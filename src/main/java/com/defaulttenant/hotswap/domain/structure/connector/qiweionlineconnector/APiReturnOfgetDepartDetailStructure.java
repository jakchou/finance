package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class APiReturnOfgetDepartDetailStructure {

    public Long errcode;
    public String errmsg;
    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Department3Structure department;
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

    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Department3Structure getDepartment() {
        return department;
    } 

    public void setDepartment(com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Department3Structure department) {
        this.department = department; 
    } 


}
