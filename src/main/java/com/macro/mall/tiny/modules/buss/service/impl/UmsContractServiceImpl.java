package com.macro.mall.tiny.modules.buss.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.macro.mall.tiny.common.constant.Constant;
import com.macro.mall.tiny.config.OrikaMapper;
import com.macro.mall.tiny.modules.buss.dto.UmsContractDto;
import com.macro.mall.tiny.modules.buss.model.UmsContract;
import com.macro.mall.tiny.modules.buss.mapper.UmsContractMapper;
import com.macro.mall.tiny.modules.buss.service.UmsContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
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
public class UmsContractServiceImpl extends ServiceImpl<UmsContractMapper, UmsContract> implements UmsContractService {

    private final String cache ="CONTRACT";
    @Autowired
    private UmsContractMapper contractMapper;
    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Override
    public void insert(UmsContractDto contractDto) {
        UmsContract contract = OrikaMapper.map(contractDto, UmsContract.class);
        contractMapper.insert(contract);
        hazelcastInstance.getMap(Constant.CACHE_LIST_NAME+cache).remove(cache);
    }

    @Override
    public List<UmsContractDto> selectList() {
        List<UmsContractDto> list = hazelcastInstance.getList(Constant.CACHE_LIST_NAME+cache);
        if(!CollectionUtils.isEmpty(list)){
            return list;
        }
        List<UmsContract> tmp = lambdaQuery().list();
        if(CollectionUtils.isEmpty(tmp)){
            return list;
        }
        list= OrikaMapper.mapList(tmp,UmsContract.class, UmsContractDto.class);
        hazelcastInstance.getMap(Constant.CACHE_LIST_NAME+cache).put(cache,list);
        return list;
    }


}
