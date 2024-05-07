package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.dto.UmsWorkDto;
import com.macro.mall.tiny.modules.buss.model.UmsWork;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 招聘表 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-29
 */
public interface UmsWorkService extends IService<UmsWork> {

    List<UmsWorkDto> listAll();

    UmsWorkDto selectById(Long id);
}
