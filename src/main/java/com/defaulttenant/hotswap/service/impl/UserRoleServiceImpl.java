package com.defaulttenant.hotswap.service.impl;

import com.defaulttenant.hotswap.service.UserRoleService;
import com.defaulttenant.hotswap.web.dto.RoleListReqDTO;
import com.defaulttenant.hotswap.web.dto.RoleListResDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;
/**
* auto generate UserRoleServiceImpl logic
* 权限下沉情况下 处理角色下用户逻辑
*
* @author sys
*/
@Service
public class UserRoleServiceImpl implements UserRoleService{

    private Logger log = LoggerFactory.getLogger(UserRoleServiceImpl.class);

    @Override
    public List<String> getUserListByRoleName(String roleName) {
        if (Objects.isNull(roleName)) {
            log.warn("查询角色下用户，角色名为空");
            return new ArrayList<>();
        }
        return Collections.emptyList();
    }

    @Override
    public List<RoleListResDTO> getRoleList(RoleListReqDTO reqDTO) {
        String nameFilter = StringUtils.isEmpty(reqDTO.getRoleName()) ? "": reqDTO.getRoleName();
        if(Objects.isNull(reqDTO.getLimit())){
            reqDTO.setLimit(2000);
        }
        if(Objects.isNull(reqDTO.getOffset())){
            reqDTO.setOffset(0);
        }
        return Collections.emptyList();
    }
}
