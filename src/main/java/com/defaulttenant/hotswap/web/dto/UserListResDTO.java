package com.defaulttenant.hotswap.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
* auto generate UserListResDTO
*
* @author sys
*/
public class UserListResDTO {
    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("Env")
    private String env;

    @JsonProperty("Source")
    private String source;

    @JsonProperty("UserId")
    private String userId;

    @Override
    public String toString() {
        return "UserData{" +
                "userName='" + userName + '\'' +
                ", env='" + env + '\'' +
                ", source='" + source + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
