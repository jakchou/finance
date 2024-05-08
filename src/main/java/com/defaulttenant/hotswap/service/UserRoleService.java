package com.defaulttenant.hotswap.service;

import java.util.List;
import com.defaulttenant.hotswap.web.dto.RoleListReqDTO;
import com.defaulttenant.hotswap.web.dto.RoleListResDTO;

/**
* auto generate UserRoleService logic
*
* @author sys
*/
public interface UserRoleService {
    /**
     * 根据角色名称获取其下的用户名称列表
     *
     * @param roleName 角色名称
     * @return 用户名称集合
     */
    List<String> getUserListByRoleName(String roleName);


    /**
     * 根据角色条件查询角色列表
     *
     * @param query 查询角色的条件
     * @return RoleListResDTO 角色集合
     */
    List<RoleListResDTO> getRoleList(RoleListReqDTO query);
}
