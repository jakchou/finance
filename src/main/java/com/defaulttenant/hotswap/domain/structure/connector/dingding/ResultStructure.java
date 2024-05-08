package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResultStructure {

    public List<String> userid_list = new ArrayList<>();
    public List<String> getUserid_list() {
        return userid_list;
    } 

    public void setUserid_list(List<String> userid_list) {
        this.userid_list = userid_list; 
    } 


}
