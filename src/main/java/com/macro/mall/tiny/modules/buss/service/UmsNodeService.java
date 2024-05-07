package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.model.UmsNode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 节点对象 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface UmsNodeService extends IService<UmsNode> {

    List<UmsNode> selectListByColumn(Long aLong);

    void deleteBatchIds(Long columnId, List<Long> nodeIds);

    void insert(Long columnId, UmsNode node);

    void updateEntity(UmsNode node);
}
