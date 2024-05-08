package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Role_listStructure {

    public String group_name;
    public String name;
    public Long id;
    public String getGroup_name() {
        return group_name;
    } 

    public void setGroup_name(String group_name) {
        this.group_name = group_name; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public Long getId() {
        return id;
    } 

    public void setId(Long id) {
        this.id = id; 
    } 


}
