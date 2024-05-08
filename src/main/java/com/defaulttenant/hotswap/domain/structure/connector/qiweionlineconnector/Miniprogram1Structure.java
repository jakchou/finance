package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Miniprogram1Structure {

    public String appid;
    public String pagepath;
    public String title;
    public String getAppid() {
        return appid;
    } 

    public void setAppid(String appid) {
        this.appid = appid; 
    } 

    public String getPagepath() {
        return pagepath;
    } 

    public void setPagepath(String pagepath) {
        this.pagepath = pagepath; 
    } 

    public String getTitle() {
        return title;
    } 

    public void setTitle(String title) {
        this.title = title; 
    } 


}
