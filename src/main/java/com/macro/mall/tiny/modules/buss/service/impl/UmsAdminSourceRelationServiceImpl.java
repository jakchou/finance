package com.macro.mall.tiny.modules.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.modules.buss.model.UmsAdminSourceRelation;
import com.macro.mall.tiny.modules.buss.mapper.UmsAdminSourceRelationMapper;
import com.macro.mall.tiny.modules.buss.service.UmsAdminSourceRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 后台用户和资源关系表 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-30
 */
@Service
public class UmsAdminSourceRelationServiceImpl extends ServiceImpl<UmsAdminSourceRelationMapper, UmsAdminSourceRelation> implements UmsAdminSourceRelationService {

    private final String cache ="U_SOURCE";

    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Override
    public void saveBatchs(List<UmsAdminSourceRelation> relationsList) {
        saveBatch(relationsList);
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).remove(relationsList.get(0).getAdminId());
    }

    @Override
    public List<UmsAdminSourceRelation> selectListByAdmin(Long adminId) {
        List<UmsAdminSourceRelation> list = (List<UmsAdminSourceRelation>) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).get(adminId);
        if(CollectionUtils.isEmpty(list)){
            list = lambdaQuery().eq(UmsAdminSourceRelation::getAdminId,adminId).list();
            hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).put(adminId,list);
        }
        return list;
    }
}
