package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.common.constant.DictType;
import com.macro.mall.tiny.modules.buss.dto.BaseDictDto;
import com.macro.mall.tiny.modules.buss.model.BaseDict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 字典类型表 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface BaseDictService extends IService<BaseDict> {

    List<BaseDictDto> listByDictType(DictType dictType);
}
