package com.defaulttenant.hotswap.task.permission.model;

/**
* 应用发布过程中，资源元数据，
*
* @author sys
* @since 2.22.0
*/
public class DeployResourceMetaData {
    private String name;
    private String description;
    private String type;
    private String createdTime;
    private String clientType;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return "DeployResourceMetaData{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", createdTime='" + createdTime + '\'' +
                ", clientType='" + clientType + '\'' +
                '}';
    }
}
