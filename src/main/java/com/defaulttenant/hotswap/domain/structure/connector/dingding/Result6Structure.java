package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Result6Structure {

    public Boolean auto_add_user;
    public Long parent_id;
    public String name;
    public Long dept_id;
    public Boolean create_dept_group;
    public Boolean getAuto_add_user() {
        return auto_add_user;
    } 

    public void setAuto_add_user(Boolean auto_add_user) {
        this.auto_add_user = auto_add_user; 
    } 

    public Long getParent_id() {
        return parent_id;
    } 

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public Long getDept_id() {
        return dept_id;
    } 

    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id; 
    } 

    public Boolean getCreate_dept_group() {
        return create_dept_group;
    } 

    public void setCreate_dept_group(Boolean create_dept_group) {
        this.create_dept_group = create_dept_group; 
    } 


}
