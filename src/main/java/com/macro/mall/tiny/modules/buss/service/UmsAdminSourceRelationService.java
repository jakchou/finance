package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.model.UmsAdminSourceRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macro.mall.tiny.modules.ums.dto.UmsResourceDto;

import java.util.List;

/**
 * <p>
 * 后台用户和资源关系表 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-30
 */
public interface UmsAdminSourceRelationService extends IService<UmsAdminSourceRelation> {

    void saveBatchs(List<UmsAdminSourceRelation> relationsList);

    List<UmsAdminSourceRelation> selectListByAdmin(Long adminId);
}
