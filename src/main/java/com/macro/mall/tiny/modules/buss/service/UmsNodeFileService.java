package com.macro.mall.tiny.modules.buss.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.tiny.modules.buss.model.UmsNodeFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 节点文件 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface UmsNodeFileService extends IService<UmsNodeFile> {

    void deleteByNodeIds(List<Long> nodeIds);

    void saveBatchs(List<UmsNodeFile> files);

    List<UmsNodeFile> selectListByNodeId(Long nodeId);
}
