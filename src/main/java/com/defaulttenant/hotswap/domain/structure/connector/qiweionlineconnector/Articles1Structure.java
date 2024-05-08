package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Articles1Structure {

    public String picurl;
    public String pagepath;
    public String appid;
    public String description;
    public String title;
    public String url;
    public String getPicurl() {
        return picurl;
    } 

    public void setPicurl(String picurl) {
        this.picurl = picurl; 
    } 

    public String getPagepath() {
        return pagepath;
    } 

    public void setPagepath(String pagepath) {
        this.pagepath = pagepath; 
    } 

    public String getAppid() {
        return appid;
    } 

    public void setAppid(String appid) {
        this.appid = appid; 
    } 

    public String getDescription() {
        return description;
    } 

    public void setDescription(String description) {
        this.description = description; 
    } 

    public String getTitle() {
        return title;
    } 

    public void setTitle(String title) {
        this.title = title; 
    } 

    public String getUrl() {
        return url;
    } 

    public void setUrl(String url) {
        this.url = url; 
    } 


}
