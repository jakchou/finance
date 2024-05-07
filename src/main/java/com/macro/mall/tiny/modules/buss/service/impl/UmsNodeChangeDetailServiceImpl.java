package com.macro.mall.tiny.modules.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.modules.buss.mapper.UmsNodeChangeSubmitMapper;
import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeDetail;
import com.macro.mall.tiny.modules.buss.mapper.UmsNodeChangeDetailMapper;
import com.macro.mall.tiny.modules.buss.service.UmsNodeChangeDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 节点对象变更表 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class UmsNodeChangeDetailServiceImpl extends ServiceImpl<UmsNodeChangeDetailMapper, UmsNodeChangeDetail> implements UmsNodeChangeDetailService {

    @Autowired
    private UmsNodeChangeDetailMapper changeDetailMapper;

    @Override
    public void insert(UmsNodeChangeDetail changeDetail) {
        changeDetailMapper.insert(changeDetail);
    }

    @Override
    public List<UmsNodeChangeDetail> selectListBySumitId(Long submitId) {
        QueryWrapper<UmsNodeChangeDetail> changeWrapper= new QueryWrapper<>();
        changeWrapper.eq("submit_id",submitId);
        return changeDetailMapper.selectList(changeWrapper);
    }


}
