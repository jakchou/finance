package com.defaulttenant.hotswap.web.dto;

/**
* auto generate UserListReqDTO
*
* @author sys
*/
public class UserListReqDTO {
    /**
     * 根据这个字段模糊查询用户
     */
    private String userNameFilter;

    /**
     * 查询哪个环境
     */
    private String appEnv;

    /**
     * 应用登录状态的token
     */
    private String token;

    /**
     * 分页参数
     */
    private Integer limit;
    private Integer offset;

    @Override
    public String toString() {
        return "UserListReqDTO{" +
                "userNameFilter='" + userNameFilter + '\'' +
                ", appEnv='" + appEnv + '\'' +
                ", token='" + token + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserNameFilter() {
        return userNameFilter;
    }

    public void setUserNameFilter(String userNameFilter) {
        this.userNameFilter = userNameFilter;
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
