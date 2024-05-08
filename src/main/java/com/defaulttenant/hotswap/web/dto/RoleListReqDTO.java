package com.defaulttenant.hotswap.web.dto;

/**
* auto generate RoleListReqDTO
*
* @author sys
*/
public class RoleListReqDTO {
    /**
     * 根据这个字段模糊查询角色
     */
    private String roleName;
    /**
     * 查询哪个环境
     */
    private String appEnv;
    /**
     * 分页参数
     */
    private Integer limit;
    private Integer offset;

    @Override
    public String toString() {
        return "RoleListReqDTO{" +
                "roleName='" + roleName + '\'' +
                ", appEnv='" + appEnv + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAppEnv() {
        return appEnv;
    }

    public void setAppEnv(String appEnv) {
        this.appEnv = appEnv;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
