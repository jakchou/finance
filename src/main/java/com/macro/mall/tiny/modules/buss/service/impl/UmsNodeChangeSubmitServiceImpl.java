package com.macro.mall.tiny.modules.buss.service.impl;

import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeSubmit;
import com.macro.mall.tiny.modules.buss.mapper.UmsNodeChangeSubmitMapper;
import com.macro.mall.tiny.modules.buss.service.UmsNodeChangeSubmitService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 节点对象变更申请 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class UmsNodeChangeSubmitServiceImpl extends ServiceImpl<UmsNodeChangeSubmitMapper, UmsNodeChangeSubmit> implements UmsNodeChangeSubmitService {

    @Autowired
    private UmsNodeChangeSubmitMapper submitMapper;

    @Override
    public void insert(UmsNodeChangeSubmit submit) {
        submitMapper.insert(submit);
    }

    @Override
    public UmsNodeChangeSubmit selectById(Long submitId) {
        return submitMapper.selectById(submitId);
    }

    @Override
    public void updateEntityById(UmsNodeChangeSubmit submit) {
        submitMapper.updateById(submit);
    }
}
