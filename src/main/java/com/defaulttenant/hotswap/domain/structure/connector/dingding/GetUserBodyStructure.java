package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class GetUserBodyStructure {

    public String userid;
    public String getUserid() {
        return userid;
    } 

    public void setUserid(String userid) {
        this.userid = userid; 
    } 


}
