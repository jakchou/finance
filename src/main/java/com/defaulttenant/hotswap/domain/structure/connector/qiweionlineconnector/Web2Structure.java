package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Web2Structure {

    public String url;
    public String title;
    public String getUrl() {
        return url;
    } 

    public void setUrl(String url) {
        this.url = url; 
    } 

    public String getTitle() {
        return title;
    } 

    public void setTitle(String title) {
        this.title = title; 
    } 


}
