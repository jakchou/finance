package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class DepartmentStructure {

    public Long id;
    public String name;
    public String name_en;
    public List<String> department_leader = new ArrayList<>();
    public Long parentid;
    public Long order;
    public Long getId() {
        return id;
    } 

    public void setId(Long id) {
        this.id = id; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public String getName_en() {
        return name_en;
    } 

    public void setName_en(String name_en) {
        this.name_en = name_en; 
    } 

    public List<String> getDepartment_leader() {
        return department_leader;
    } 

    public void setDepartment_leader(List<String> department_leader) {
        this.department_leader = department_leader; 
    } 

    public Long getParentid() {
        return parentid;
    } 

    public void setParentid(Long parentid) {
        this.parentid = parentid; 
    } 

    public Long getOrder() {
        return order;
    } 

    public void setOrder(Long order) {
        this.order = order; 
    } 


}
