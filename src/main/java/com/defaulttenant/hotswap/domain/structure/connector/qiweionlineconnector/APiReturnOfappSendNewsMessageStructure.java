package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class APiReturnOfappSendNewsMessageStructure {

    public Long errcode;
    public String errmsg;
    public String invaliduser;
    public String invalidparty;
    public String invalidtag;
    public String unlicenseduser;
    public String msgid;
    public String response_code;
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

    public String getInvaliduser() {
        return invaliduser;
    } 

    public void setInvaliduser(String invaliduser) {
        this.invaliduser = invaliduser; 
    } 

    public String getInvalidparty() {
        return invalidparty;
    } 

    public void setInvalidparty(String invalidparty) {
        this.invalidparty = invalidparty; 
    } 

    public String getInvalidtag() {
        return invalidtag;
    } 

    public void setInvalidtag(String invalidtag) {
        this.invalidtag = invalidtag; 
    } 

    public String getUnlicenseduser() {
        return unlicenseduser;
    } 

    public void setUnlicenseduser(String unlicenseduser) {
        this.unlicenseduser = unlicenseduser; 
    } 

    public String getMsgid() {
        return msgid;
    } 

    public void setMsgid(String msgid) {
        this.msgid = msgid; 
    } 

    public String getResponse_code() {
        return response_code;
    } 

    public void setResponse_code(String response_code) {
        this.response_code = response_code; 
    } 


}
