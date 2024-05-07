package com.macro.mall.tiny.modules.buss.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.modules.buss.dto.UmsDepartmentDto;
import com.macro.mall.tiny.modules.buss.model.UmsDepartment;
import com.macro.mall.tiny.modules.buss.mapper.UmsDepartmentMapper;
import com.macro.mall.tiny.modules.buss.service.UmsDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-04-02
 */
@Service
public class UmsDepartmentServiceImpl extends ServiceImpl<UmsDepartmentMapper, UmsDepartment> implements UmsDepartmentService {
    @Autowired
    private UmsDepartmentMapper departmentMapper;
    private static final String CACHE_NAME = "DEPARTMENT";

    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Override
    public List<UmsDepartmentDto> selectList() {
        List<UmsDepartmentDto> list = (List<UmsDepartmentDto>) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+CACHE_NAME).get(CACHE_NAME);
        if(!CollectionUtils.isEmpty(list)){
            return list;
        }
        List<UmsDepartment> tmp = lambdaQuery().orderByAsc(UmsDepartment::getId).list();
        if(CollectionUtils.isEmpty(tmp)){
            return list;
        }

        list = OrikaMapper.mapList(tmp,UmsDepartment.class,UmsDepartmentDto.class);
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+CACHE_NAME).put(CACHE_NAME,list);
        return list;
    }
}
