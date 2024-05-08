package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class APiReturnOfgetDepartmentListStructure {

    public Long errcode;
    public String errmsg;
    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Department2Structure> department = new ArrayList<>();
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

    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Department2Structure> getDepartment() {
        return department;
    } 

    public void setDepartment(List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Department2Structure> department) {
        this.department = department; 
    } 


}
