package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class AppSendMessageBodyStructure {

    public String touser;
    public String toparty;
    public String totag;
    public String msgtype;
    public Long agentid;
    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Text7Structure text;
    public Long safe;
    public Long enable_id_trans;
    public Long enable_duplicate_check;
    public Long duplicate_check_interval;
    public String getTouser() {
        return touser;
    } 

    public void setTouser(String touser) {
        this.touser = touser; 
    } 

    public String getToparty() {
        return toparty;
    } 

    public void setToparty(String toparty) {
        this.toparty = toparty; 
    } 

    public String getTotag() {
        return totag;
    } 

    public void setTotag(String totag) {
        this.totag = totag; 
    } 

    public String getMsgtype() {
        return msgtype;
    } 

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype; 
    } 

    public Long getAgentid() {
        return agentid;
    } 

    public void setAgentid(Long agentid) {
        this.agentid = agentid; 
    } 

    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Text7Structure getText() {
        return text;
    } 

    public void setText(com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Text7Structure text) {
        this.text = text; 
    } 

    public Long getSafe() {
        return safe;
    } 

    public void setSafe(Long safe) {
        this.safe = safe; 
    } 

    public Long getEnable_id_trans() {
        return enable_id_trans;
    } 

    public void setEnable_id_trans(Long enable_id_trans) {
        this.enable_id_trans = enable_id_trans; 
    } 

    public Long getEnable_duplicate_check() {
        return enable_duplicate_check;
    } 

    public void setEnable_duplicate_check(Long enable_duplicate_check) {
        this.enable_duplicate_check = enable_duplicate_check; 
    } 

    public Long getDuplicate_check_interval() {
        return duplicate_check_interval;
    } 

    public void setDuplicate_check_interval(Long duplicate_check_interval) {
        this.duplicate_check_interval = duplicate_check_interval; 
    } 


}
