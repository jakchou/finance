package com.macro.mall.tiny.modules.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.common.constant.DictType;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.modules.buss.dto.BaseDictDto;
import com.macro.mall.tiny.modules.buss.dto.UmsDepartmentDto;
import com.macro.mall.tiny.modules.buss.mapper.UmsDepartmentMapper;
import com.macro.mall.tiny.modules.buss.model.BaseDict;
import com.macro.mall.tiny.modules.buss.mapper.BaseDictMapper;
import com.macro.mall.tiny.modules.buss.model.UmsDepartment;
import com.macro.mall.tiny.modules.buss.service.BaseDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 字典类型表 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class BaseDictServiceImpl extends ServiceImpl<BaseDictMapper, BaseDict> implements BaseDictService {

    @Autowired
    private BaseDictMapper dictMapper;

    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Override
    public List<BaseDictDto> listByDictType(DictType dictType) {
        List<BaseDictDto> list = (List<BaseDictDto>) hazelcastInstance.getMap(Constant.CACHE_IMAP_NAME+"DICT").get(dictType.getType());
        if(!CollectionUtils.isEmpty(list)){
            return list;
        }
        List<BaseDict> tmp = lambdaQuery().eq(BaseDict::getDictType, dictType.getType()).orderByAsc(BaseDict::getDictSort).list();
        if(CollectionUtils.isEmpty(tmp)){
            return new ArrayList<>();
        }
        list  = OrikaMapper.mapList(tmp,BaseDict.class,BaseDictDto.class);
        hazelcastInstance.getMap(Constant.CACHE_LIST_NAME+"DICT").put(dictType.getType(),list);
        return list;
    }

}
