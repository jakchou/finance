package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class APiReturnOfgetUserDetailStructure {

    public Long errcode;
    public String errmsg;
    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Userlist2Structure> userlist = new ArrayList<>();
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

    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Userlist2Structure> getUserlist() {
        return userlist;
    } 

    public void setUserlist(List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Userlist2Structure> userlist) {
        this.userlist = userlist; 
    } 


}
