package com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Userlist2Structure {

    public String address;
    public String gender;
    public String external_position;
    public String mobile;
    public List<Long> is_leader_in_dept = new ArrayList<>();
    public String telephone;
    public List<String> direct_leader = new ArrayList<>();
    public String avatar;
    public Long main_department;
    public String userid;
    public String english_name;
    public String thumb_avatar;
    public String name;
    public String alias;
    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Extattr1Structure extattr;
    public String qr_code;
    public String position;
    public List<Long> department = new ArrayList<>();
    public String open_userid;
    public String biz_mail;
    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.External_profile1Structure external_profile;
    public String email;
    public List<Long> order = new ArrayList<>();
    public Long status;
    public String getAddress() {
        return address;
    } 

    public void setAddress(String address) {
        this.address = address; 
    } 

    public String getGender() {
        return gender;
    } 

    public void setGender(String gender) {
        this.gender = gender; 
    } 

    public String getExternal_position() {
        return external_position;
    } 

    public void setExternal_position(String external_position) {
        this.external_position = external_position; 
    } 

    public String getMobile() {
        return mobile;
    } 

    public void setMobile(String mobile) {
        this.mobile = mobile; 
    } 

    public List<Long> getIs_leader_in_dept() {
        return is_leader_in_dept;
    } 

    public void setIs_leader_in_dept(List<Long> is_leader_in_dept) {
        this.is_leader_in_dept = is_leader_in_dept; 
    } 

    public String getTelephone() {
        return telephone;
    } 

    public void setTelephone(String telephone) {
        this.telephone = telephone; 
    } 

    public List<String> getDirect_leader() {
        return direct_leader;
    } 

    public void setDirect_leader(List<String> direct_leader) {
        this.direct_leader = direct_leader; 
    } 

    public String getAvatar() {
        return avatar;
    } 

    public void setAvatar(String avatar) {
        this.avatar = avatar; 
    } 

    public Long getMain_department() {
        return main_department;
    } 

    public void setMain_department(Long main_department) {
        this.main_department = main_department; 
    } 

    public String getUserid() {
        return userid;
    } 

    public void setUserid(String userid) {
        this.userid = userid; 
    } 

    public String getEnglish_name() {
        return english_name;
    } 

    public void setEnglish_name(String english_name) {
        this.english_name = english_name; 
    } 

    public String getThumb_avatar() {
        return thumb_avatar;
    } 

    public void setThumb_avatar(String thumb_avatar) {
        this.thumb_avatar = thumb_avatar; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public String getAlias() {
        return alias;
    } 

    public void setAlias(String alias) {
        this.alias = alias; 
    } 

    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Extattr1Structure getExtattr() {
        return extattr;
    } 

    public void setExtattr(com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.Extattr1Structure extattr) {
        this.extattr = extattr; 
    } 

    public String getQr_code() {
        return qr_code;
    } 

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code; 
    } 

    public String getPosition() {
        return position;
    } 

    public void setPosition(String position) {
        this.position = position; 
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

    public String getBiz_mail() {
        return biz_mail;
    } 

    public void setBiz_mail(String biz_mail) {
        this.biz_mail = biz_mail; 
    } 

    public com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.External_profile1Structure getExternal_profile() {
        return external_profile;
    } 

    public void setExternal_profile(com.defaulttenant.hotswap.domain.structure.connector.qiweionlineconnector.External_profile1Structure external_profile) {
        this.external_profile = external_profile; 
    } 

    public String getEmail() {
        return email;
    } 

    public void setEmail(String email) {
        this.email = email; 
    } 

    public List<Long> getOrder() {
        return order;
    } 

    public void setOrder(List<Long> order) {
        this.order = order; 
    } 

    public Long getStatus() {
        return status;
    } 

    public void setStatus(Long status) {
        this.status = status; 
    } 


}
