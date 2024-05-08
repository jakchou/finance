package com.defaulttenant.hotswap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author jingzhixing
 * @date 2022/1/17
 * <p>
 * 对接远程用户中心需求
 * 客户配置的远程用户中心访问地址以及登录页地址
 */
@Component
@ConfigurationProperties(prefix = "product.remoteusercenter")
public class RemoteUserCenterProperties {
    /**
     * 是否切换制品应用的用户中心
     * true为切换
     */
    private Boolean change = false;

    /**
     * 制品应用类型以及对应的用户中心服务地址
     */
    private String address;

    /**
     * 制品应用对接的远程用户中心的登陆页配置详情
     * 用于前端跳转
     * 值内容举例如下
     * url: xuetong/login.html
     * params:{
     *     client_id:
     * }
     */
    private LoginDetail pc;

    private LoginDetail h5;

    /**
     * 对接远程用户中心时
     * 给用户分配的默认租户
     * 联调环境配置为 lcp
     * 测试环境配置为 defaultTenant
     * 线上环境 关闭change开关
     * 部署到客户后 该值填客户环境的默认租户
     */
    private String tenant;

    public Boolean getChange() {
        return change;
    }

    public void setChange(Boolean change) {
        this.change = change;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LoginDetail getPc() {
        return pc;
    }

    public void setPc(LoginDetail pc) {
        this.pc = pc;
    }

    public LoginDetail getH5() {
        return h5;
    }

    public void setH5(LoginDetail h5) {
        this.h5 = h5;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String toString() {
        return "RemoteUserCenterProperties{" +
                "change=" + change +
                ", address='" + address + '\'' +
                ", pc=" + pc +
                ", h5=" + h5 +
                ", tenant='" + tenant + '\'' +
                '}';
    }

    public static class LoginDetail {
        private String url;
        private Map<String, String> params;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        @Override
        public String toString() {
            return "LoginDetail{" +
                    "url='" + url + '\'' +
                    ", params=" + params +
                    '}';
        }
    }
}
