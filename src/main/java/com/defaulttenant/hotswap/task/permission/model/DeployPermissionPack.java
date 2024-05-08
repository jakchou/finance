package com.defaulttenant.hotswap.task.permission.model;

import java.util.List;
import java.util.Map;

/**
* 应用发布过程中 权限数据包
*
* @author sys
* @since 2.22.0
*/
public class DeployPermissionPack {
    private Map<String, List<List<DeployLogicAuthMetaData>>> logicAuthMetaDataCollect;
    private List<DeployPermissionMetaData> permissionMetaDataCollect;
    private List<DeployResourceMetaData> resourceMetaDataCollect;
    private List<DeployRoleMetaData> roleMetaDataCollect;
    private List<DeployRoleResourceMetaData> roleResourceMetaDataCollect;
    private List<DeployUserMetaData> userMetaDataCollect;

    public Map<String, List<List<DeployLogicAuthMetaData>>> getLogicAuthMetaDataCollect() {
        return logicAuthMetaDataCollect;
    }

    public void setLogicAuthMetaDataCollect(Map<String, List<List<DeployLogicAuthMetaData>>> logicAuthMetaDataCollect) {
        this.logicAuthMetaDataCollect = logicAuthMetaDataCollect;
    }

    public List<DeployPermissionMetaData> getPermissionMetaDataCollect() {
        return permissionMetaDataCollect;
    }

    public void setPermissionMetaDataCollect(List<DeployPermissionMetaData> permissionMetaDataCollect) {
        this.permissionMetaDataCollect = permissionMetaDataCollect;
    }

    public List<DeployResourceMetaData> getResourceMetaDataCollect() {
        return resourceMetaDataCollect;
    }

    public void setResourceMetaDataCollect(List<DeployResourceMetaData> resourceMetaDataCollect) {
        this.resourceMetaDataCollect = resourceMetaDataCollect;
    }

    public List<DeployRoleMetaData> getRoleMetaDataCollect() {
        return roleMetaDataCollect;
    }

    public void setRoleMetaDataCollect(List<DeployRoleMetaData> roleMetaDataCollect) {
        this.roleMetaDataCollect = roleMetaDataCollect;
    }

    public List<DeployRoleResourceMetaData> getRoleResourceMetaDataCollect() {
        return roleResourceMetaDataCollect;
    }

    public void setRoleResourceMetaDataCollect(List<DeployRoleResourceMetaData> roleResourceMetaDataCollect) {
        this.roleResourceMetaDataCollect = roleResourceMetaDataCollect;
    }

    public List<DeployUserMetaData> getUserMetaDataCollect() {
        return userMetaDataCollect;
    }

    public void setUserMetaDataCollect(List<DeployUserMetaData> userMetaDataCollect) {
        this.userMetaDataCollect = userMetaDataCollect;
    }

    @Override
    public String toString() {
        return "DeployPermissionPack{" +
                "logicAuthMetaDataCollect=" + logicAuthMetaDataCollect +
                ", permissionMetaDataCollect=" + permissionMetaDataCollect +
                ", resourceMetaDataCollect=" + resourceMetaDataCollect +
                ", roleMetaDataCollect=" + roleMetaDataCollect +
                ", roleResourceMetaDataCollect=" + roleResourceMetaDataCollect +
                ", userMetaDataCollect=" + userMetaDataCollect +
                '}';
    }
}
