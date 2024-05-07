package com.macro.mall.tiny.modules.buss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeFile;
import com.macro.mall.tiny.modules.buss.mapper.UmsNodeChangeFileMapper;
import com.macro.mall.tiny.modules.buss.service.UmsNodeChangeFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变更节点文件 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class UmsNodeChangeFileServiceImpl extends ServiceImpl<UmsNodeChangeFileMapper, UmsNodeChangeFile> implements UmsNodeChangeFileService {

    @Autowired
    private UmsNodeChangeFileMapper changeFileMapper;
    @Override
    public void saveBatchs(List<UmsNodeChangeFile> fileList) {
        saveBatch(fileList);
    }

    @Override
    public List<UmsNodeChangeFile> selectListByNodeIds(List<Long> ids) {
        QueryWrapper<UmsNodeChangeFile> changeFileWrapper= new QueryWrapper<>();
        changeFileWrapper.in("node_id",ids);
        return changeFileMapper.selectList(changeFileWrapper);
    }

    @Override
    public void deleteByNodeIds(List<Long> exitChangeIds) {
        QueryWrapper<UmsNodeChangeFile> queryWrapper =new QueryWrapper();
        queryWrapper.in("node_id",exitChangeIds);
        changeFileMapper.delete(queryWrapper);
    }

}
