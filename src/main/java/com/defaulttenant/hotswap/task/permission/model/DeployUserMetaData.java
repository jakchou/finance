package com.defaulttenant.hotswap.task.permission.model;

/**
* 应用发布时 测试账号的元数据(主要是用户名称、密码、来源等)
*
* @author sys
* @since 2.22.0
*/
public class DeployUserMetaData {
    private String userId;
    private String userName;
    private String password;
    private String displayName;
    private String createdTime;
    private String source;
    private String status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeployUserMetaData{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
