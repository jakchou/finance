package com.defaulttenant.hotswap.task.permission.model;

/**
* 应用发布过程中 权限元数据，权限数据和角色是一一对应的
*
* @author sys
* @since 2.22.0
*/
public class DeployPermissionMetaData {
    private String name;
    private String description;
    private String createdTime;

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

    @Override
    public String toString() {
        return "DeployPermissionMetaData{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdTime='" + createdTime + '\'' +
                '}';
    }
}
