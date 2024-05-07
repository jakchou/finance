package com.macro.mall.tiny.modules.buss.service.impl;

import com.macro.mall.tiny.modules.buss.model.UmsWorkChange;
import com.macro.mall.tiny.modules.buss.mapper.UmsWorkChangeMapper;
import com.macro.mall.tiny.modules.buss.service.UmsWorkChangeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 招聘变更表 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-29
 */
@Service
public class UmsWorkChangeServiceImpl extends ServiceImpl<UmsWorkChangeMapper, UmsWorkChange> implements UmsWorkChangeService {

    @Autowired
    private UmsWorkChangeMapper workChangeMapper;

    @Override
    public void insert(UmsWorkChange changeDetail) {
        workChangeMapper.insert(changeDetail);
    }

    @Override
    public void deleteById(Long id) {
        workChangeMapper.deleteById(id);
    }

    @Override
    public UmsWorkChange selectBySubmit(Long id) {
        return lambdaQuery().eq(UmsWorkChange::getSubmitId,id).one();
    }
}
