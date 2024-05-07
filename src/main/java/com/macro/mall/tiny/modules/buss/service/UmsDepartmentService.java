package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.dto.UmsDepartmentDto;
import com.macro.mall.tiny.modules.buss.model.UmsDepartment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author macro
 * @since 2024-04-02
 */
public interface UmsDepartmentService extends IService<UmsDepartment> {

    List<UmsDepartmentDto> selectList();
}
