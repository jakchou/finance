package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class UserlistStructure {

    public String name;
    public List<Long> department = new ArrayList<>();
    public String open_userid;
    public String userid;
    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public List<Long> getDepartment() {
        return department;
    } 

    public void setDepartment(List<Long> department) {
        this.department = department; 
    } 

    public String getOpen_userid() {
        return open_userid;
    } 

    public void setOpen_userid(String open_userid) {
        this.open_userid = open_userid; 
    } 

    public String getUserid() {
        return userid;
    } 

    public void setUserid(String userid) {
        this.userid = userid; 
    } 


}
