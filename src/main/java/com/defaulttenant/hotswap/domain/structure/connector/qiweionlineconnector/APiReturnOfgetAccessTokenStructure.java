package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class APiReturnOfgetAccessTokenStructure {

    public Long errcode;
    public String errmsg;
    public String access_token;
    public Long expires_in;
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

    public String getAccess_token() {
        return access_token;
    } 

    public void setAccess_token(String access_token) {
        this.access_token = access_token; 
    } 

    public Long getExpires_in() {
        return expires_in;
    } 

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in; 
    } 


}
