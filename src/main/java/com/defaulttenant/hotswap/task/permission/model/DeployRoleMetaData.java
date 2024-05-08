package com.defaulttenant.hotswap.task.permission.model;


/**
* 应用发布时IDE内的角色数据
*
* @author sys
* @since 2.22.0
*/
public class DeployRoleMetaData {
    private String name;
    private String description;
    private String createdTime;
    private String editable;
    private String roleStatus;
    private String uuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public String getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(String roleStatus) {
        this.roleStatus = roleStatus;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "DeployRoleMetaData{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", editable='" + editable + '\'' +
                ", roleStatus='" + roleStatus + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
