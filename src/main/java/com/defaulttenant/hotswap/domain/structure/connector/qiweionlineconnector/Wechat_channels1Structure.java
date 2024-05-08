package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Wechat_channels1Structure {

    public String nickname;
    public Long status;
    public String getNickname() {
        return nickname;
    } 

    public void setNickname(String nickname) {
        this.nickname = nickname; 
    } 

    public Long getStatus() {
        return status;
    } 

    public void setStatus(Long status) {
        this.status = status; 
    } 


}
