package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.model.UmsWorkChange;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 招聘变更表 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-29
 */
public interface UmsWorkChangeService extends IService<UmsWorkChange> {

    void insert(UmsWorkChange changeDetail);

    void deleteById(Long id);

    UmsWorkChange selectBySubmit(Long id);
}
