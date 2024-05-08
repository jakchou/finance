package com.defaulttenant.hotswap.context;

import java.util.Objects;
import com.defaulttenant.hotswap.util.BCryptHelper;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 全局用户信息上下文
 * 也用于nasl.auth命名空间处理
 */
public class UserContext {
    private static final ThreadLocal<UserInfo> currentUserLocal = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> ifRemoteUserCenter = new ThreadLocal<>();

    /**
     * 获取当前登录用户信息UserInfo
     * 如果认证过期，则返回Null
     *
     * @return
     */
    public static UserInfo getCurrentUserInfo() {
        return currentUserLocal.get();
    }

    /**
     * 设置用户信息
     */
    public static void setCurrentUserInfo(UserInfo userInfo){
        currentUserLocal.set(userInfo);
    }

    /**
     * 获取当前登录用户是否为远程用户认证中心校验token
     *
     * @return Boolean
     */
    public static Boolean getIfRemoteUserCenter() {
        return ifRemoteUserCenter.get();
    }

    /**
     * 设置当前登录用户是否为远程用户认证中心校验token
     */
    public static void setIfRemoteUserCenter(Boolean isRemoteUserCenter){
        ifRemoteUserCenter.set(isRemoteUserCenter);
    }

    /**
     * 获取当前登录用户UserName
     * 如果认证过期，则返回Null
     *
     * @return
     */
    public static String getCurrentUserName() {
        UserInfo info = currentUserLocal.get();
        if (Objects.nonNull(info)) {
            return info.getUserName();
        }
        return null;
    }


    /**
    * 采用Bcrypt算法进行加密
    * Bcrypt Hash Generator
    *
    * @param password
    * @return
    */
    public static String encryptPassword(String password) {
        return BCryptHelper.hashpw(password, BCryptHelper.gensalt());
    }

    /**
    * 采用Bcrypt算法进行校验
    *
    * @param plainText
    * @param hashedText
    * @return
    */
    public static Boolean validatePassword(String plainText, String hashedText) {
        if(StringUtils.isEmpty(plainText)||StringUtils.isEmpty(hashedText)){
            return false;
        }
        return BCryptHelper.checkpw(plainText, hashedText.replace("BCRYPT(",""));
    }

    /**
    * 生成用户唯一标识
    * @param userName 账号名
    * @param source 认证源
    * @return
    */
    public static String generateUserId(String userName,String source){
        return DigestUtils.md5DigestAsHex((userName + source).getBytes());
    }

    /**
     * 后端用户相关系统逻辑getCurrentUser
     * 如果已认证过，则返回UserInfo
     * 如果认证过期，则返回Null
     *
     * @return UserInfo
     */
    public static UserInfo getCurrentUser() {
        UserInfo info = currentUserLocal.get();
        if (Objects.nonNull(info)) {
            return info;
        }
        return null;
    }

    public static void clear() {
        currentUserLocal.remove();
        ifRemoteUserCenter.remove();
    }


    public static class UserInfo {
        public String userId;    //用户ID
        public String userName;  //账户名
        public String email;     //邮箱
        public String phone;     //手机号
        public String nickName;  //昵称
        public String source;    //来源
        public Long createTime;//创建时间
        public Long updateTime;//修改时间
        public String status;    //用户状态
        public String extraInfo; //扩展信息，一般为json格式

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
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

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public Long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Long updateTime) {
            this.updateTime = updateTime;
        }

        public String getExtraInfo() {
            return extraInfo;
        }

        public void setExtraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
        }
    }
}
