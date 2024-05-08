package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class External_profileStructure {

    public String external_corp_name;
    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Wechat_channelsStructure wechat_channels;
    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.External_attrStructure> external_attr = new ArrayList<>();
    public String getExternal_corp_name() {
        return external_corp_name;
    } 

    public void setExternal_corp_name(String external_corp_name) {
        this.external_corp_name = external_corp_name; 
    } 

    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Wechat_channelsStructure getWechat_channels() {
        return wechat_channels;
    } 

    public void setWechat_channels(com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Wechat_channelsStructure wechat_channels) {
        this.wechat_channels = wechat_channels; 
    } 

    public List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.External_attrStructure> getExternal_attr() {
        return external_attr;
    } 

    public void setExternal_attr(List<com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.External_attrStructure> external_attr) {
        this.external_attr = external_attr; 
    } 


}
