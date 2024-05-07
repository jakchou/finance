/*
package com.macro.mall.tiny.modules.ums.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

import com.macro.mall.tiny.modules.ums.mapper.UmsAdminMapper;
import com.macro.mall.tiny.modules.ums.model.UmsAdmin;
import com.macro.mall.tiny.modules.ums.model.UmsAdminRoleRelation;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import com.macro.mall.tiny.modules.ums.service.UmsAdminRoleRelationService;
import com.macro.mall.tiny.modules.ums.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

*/
/**
 * 后台用户缓存管理Service实现类
 * Created by macro on 2020/3/13.
 *//*

@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private UmsAdminRoleRelationService adminRoleRelationService;

    @Override
    public void delAdmin(Long adminId) {
        UmsAdmin admin = adminService.getById(adminId);
        if (admin != null) {
        }
    }

    @Override
    public void delResourceList(Long adminId) {
    }

    @Override
    public void delResourceListByRole(Long roleId) {
        QueryWrapper<UmsAdminRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdminRoleRelation::getRoleId,roleId);
        List<UmsAdminRoleRelation> relationList = adminRoleRelationService.list(wrapper);
    }

    @Override
    public void delResourceListByRoleIds(List<Long> roleIds) {
        QueryWrapper<UmsAdminRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(UmsAdminRoleRelation::getRoleId,roleIds);
        List<UmsAdminRoleRelation> relationList = adminRoleRelationService.list(wrapper);
    }

    @Override
    public void delResourceListByResource(Long resourceId) {
        List<Long> adminIdList = adminMapper.getAdminIdList(resourceId);
    }

    @Override
    public UmsAdmin getAdmin(String username) {
        return null;
    }

    @Override
    public void setAdmin(UmsAdmin admin) {
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        return null;
    }

    @Override
    public void setResourceList(Long adminId, List<UmsResource> resourceList) {
    }
}
*/
