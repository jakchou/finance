package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 节点对象变更表 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface UmsNodeChangeDetailService extends IService<UmsNodeChangeDetail> {

    void insert(UmsNodeChangeDetail changeDetail);

    List<UmsNodeChangeDetail> selectListBySumitId(Long submitId);
}
