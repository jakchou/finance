package com.macro.mall.tiny.modules.buss.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.modules.buss.dto.UmsWorkDto;
import com.macro.mall.tiny.modules.buss.model.UmsWork;
import com.macro.mall.tiny.modules.buss.mapper.UmsWorkMapper;
import com.macro.mall.tiny.modules.buss.service.UmsWorkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 招聘表 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-29
 */
@Service
public class UmsWorkServiceImpl extends ServiceImpl<UmsWorkMapper, UmsWork> implements UmsWorkService {

    private  final String cache ="work";
    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Override
    public List<UmsWorkDto> listAll() {
        List<UmsWorkDto> workDtoList= (List<UmsWorkDto>) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).get(cache);
        if(!CollectionUtils.isEmpty(workDtoList)){
            return workDtoList;
        }
        List<UmsWork> workList = lambdaQuery().orderByDesc(UmsWork::getId).list();
        if(!CollectionUtils.isEmpty(workList)){
            return workDtoList;
        }
        workDtoList = OrikaMapper.mapList(workList, UmsWork.class,UmsWorkDto.class);
        hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+cache).put(cache,workDtoList);
        return workDtoList;
    }

    @Override
    public UmsWorkDto selectById(Long id) {
        List<UmsWorkDto> workDtoList = listAll();
        return workDtoList.stream().filter(workDto -> workDto.getId().equals(id)).findFirst().orElse(null);
    }
}
