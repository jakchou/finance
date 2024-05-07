package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeSubmit;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 节点对象变更申请 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface UmsNodeChangeSubmitService extends IService<UmsNodeChangeSubmit> {

    void insert(UmsNodeChangeSubmit submit);

    UmsNodeChangeSubmit selectById(Long submitId);

    void updateEntityById(UmsNodeChangeSubmit submit);
}
