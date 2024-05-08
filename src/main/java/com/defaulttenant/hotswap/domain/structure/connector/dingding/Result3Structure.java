package com.defaulttenant.hotswap.domain.structure.connector.dingding; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Result3Structure {

    public Boolean active;
    public Boolean admin;
    public String avatar;
    public Boolean boss;
    public List<Long> dept_id_list = new ArrayList<>();
    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Dept_order_listStructure> dept_order_list = new ArrayList<>();
    public Boolean exclusive_account;
    public Boolean hide_mobile;
    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Leader_in_deptStructure> leader_in_dept = new ArrayList<>();
    public String mobile;
    public String name;
    public Boolean real_authed;
    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Role_listStructure> role_list = new ArrayList<>();
    public Boolean senior;
    public String state_code;
    public String unionid;
    public String userid;
    public Boolean getActive() {
        return active;
    } 

    public void setActive(Boolean active) {
        this.active = active; 
    } 

    public Boolean getAdmin() {
        return admin;
    } 

    public void setAdmin(Boolean admin) {
        this.admin = admin; 
    } 

    public String getAvatar() {
        return avatar;
    } 

    public void setAvatar(String avatar) {
        this.avatar = avatar; 
    } 

    public Boolean getBoss() {
        return boss;
    } 

    public void setBoss(Boolean boss) {
        this.boss = boss; 
    } 

    public List<Long> getDept_id_list() {
        return dept_id_list;
    } 

    public void setDept_id_list(List<Long> dept_id_list) {
        this.dept_id_list = dept_id_list; 
    } 

    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Dept_order_listStructure> getDept_order_list() {
        return dept_order_list;
    } 

    public void setDept_order_list(List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Dept_order_listStructure> dept_order_list) {
        this.dept_order_list = dept_order_list; 
    } 

    public Boolean getExclusive_account() {
        return exclusive_account;
    } 

    public void setExclusive_account(Boolean exclusive_account) {
        this.exclusive_account = exclusive_account; 
    } 

    public Boolean getHide_mobile() {
        return hide_mobile;
    } 

    public void setHide_mobile(Boolean hide_mobile) {
        this.hide_mobile = hide_mobile; 
    } 

    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Leader_in_deptStructure> getLeader_in_dept() {
        return leader_in_dept;
    } 

    public void setLeader_in_dept(List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Leader_in_deptStructure> leader_in_dept) {
        this.leader_in_dept = leader_in_dept; 
    } 

    public String getMobile() {
        return mobile;
    } 

    public void setMobile(String mobile) {
        this.mobile = mobile; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public Boolean getReal_authed() {
        return real_authed;
    } 

    public void setReal_authed(Boolean real_authed) {
        this.real_authed = real_authed; 
    } 

    public List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Role_listStructure> getRole_list() {
        return role_list;
    } 

    public void setRole_list(List<com.defaulttenant.hotswap.domain.structure.connector.dingding.Role_listStructure> role_list) {
        this.role_list = role_list; 
    } 

    public Boolean getSenior() {
        return senior;
    } 

    public void setSenior(Boolean senior) {
        this.senior = senior; 
    } 

    public String getState_code() {
        return state_code;
    } 

    public void setState_code(String state_code) {
        this.state_code = state_code; 
    } 

    public String getUnionid() {
        return unionid;
    } 

    public void setUnionid(String unionid) {
        this.unionid = unionid; 
    } 

    public String getUserid() {
        return userid;
    } 

    public void setUserid(String userid) {
        this.userid = userid; 
    } 


}
