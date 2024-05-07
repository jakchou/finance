package com.macro.mall.tiny.modules.buss.service.impl;

import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeReview;
import com.macro.mall.tiny.modules.buss.mapper.UmsNodeChangeReviewMapper;
import com.macro.mall.tiny.modules.buss.service.UmsNodeChangeReviewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 节点对象变更审核 服务实现类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Service
public class UmsNodeChangeReviewServiceImpl extends ServiceImpl<UmsNodeChangeReviewMapper, UmsNodeChangeReview> implements UmsNodeChangeReviewService {

    @Autowired
    private UmsNodeChangeReviewMapper reviewMapper;

    @Override
    public void insert(UmsNodeChangeReview review) {
        reviewMapper.insert(review);
    }
}
