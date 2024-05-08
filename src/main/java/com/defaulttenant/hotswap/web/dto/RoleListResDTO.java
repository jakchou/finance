package com.defaulttenant.hotswap.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
* auto generate RoleListResDTO
*
* @author sys
*/
public class RoleListResDTO {

    @JsonProperty("RoleId")
    private String roleId;

    @JsonProperty("RoleName")
    private String roleName;

    @JsonProperty("Env")
    private String env;

    @Override
    public String toString() {
        return "RoleListResDTO{" +
                "roleName='" + roleName + '\'' +
                ", env='" + env + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
