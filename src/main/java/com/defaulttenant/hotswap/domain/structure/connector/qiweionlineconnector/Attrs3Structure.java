package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Attrs3Structure {

    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Web1Structure web;
    public String name;
    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Text5Structure text;
    public Long type;
    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Web1Structure getWeb() {
        return web;
    } 

    public void setWeb(com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Web1Structure web) {
        this.web = web; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Text5Structure getText() {
        return text;
    } 

    public void setText(com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Text5Structure text) {
        this.text = text; 
    } 

    public Long getType() {
        return type;
    } 

    public void setType(Long type) {
        this.type = type; 
    } 


}
