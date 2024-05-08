package com.defaulttenant.hotswap.task.permission.model;


/**
* 应用发布时，记录不同页面或组件分配的角色信息
* 当所有页面或组件都没有绑定角色和资源的时候 此数据为空
*
* @author sys
* @since 2.22.0
*/
public class DeployRoleResourceMetaData {
    private String roleName;
    private String resourcePath;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public String toString() {
        return "DeployRoleResourceMetaData{" +
                "roleName='" + roleName + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                '}';
    }
}
