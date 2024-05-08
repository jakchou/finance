package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Result1Structure {

    public List<Long> parent_id_list = new ArrayList<>();
    public List<Long> getParent_id_list() {
        return parent_id_list;
    } 

    public void setParent_id_list(List<Long> parent_id_list) {
        this.parent_id_list = parent_id_list; 
    } 


}
