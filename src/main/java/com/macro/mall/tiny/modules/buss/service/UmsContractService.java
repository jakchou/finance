package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.dto.UmsContractDto;
import com.macro.mall.tiny.modules.buss.model.UmsContract;
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
public interface UmsContractService extends IService<UmsContract> {

    void insert(UmsContractDto contractDto);

    List<UmsContractDto> selectList();
}
