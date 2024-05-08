package com.defaulttenant.hotswap.service.impl;

import com.defaulttenant.hotswap.service.UserInfoService;
import com.defaulttenant.hotswap.web.dto.UserListReqDTO;
import com.defaulttenant.hotswap.web.dto.UserListResDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Collections;

/**
* auto generate UserInfoServiceImpl
*
* 应用内用户相关的系统逻辑
*
* @author sys
*/
@Service
public class UserInfoServiceImpl implements UserInfoService {
    private Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);


    /**
     * 用户下沉的用户获取逻辑
     *
     * @return
     */
    @Override
    public List<UserListResDTO> getUserListFromAppOrNuims(UserListReqDTO reqDTO) {
        return Collections.emptyList();
    }
}
