package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeReview;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 节点对象变更审核 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface UmsNodeChangeReviewService extends IService<UmsNodeChangeReview> {

    void insert(UmsNodeChangeReview review);
}
