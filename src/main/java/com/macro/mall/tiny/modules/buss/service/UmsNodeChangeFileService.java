package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.model.UmsNodeChangeFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 变更节点文件 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface UmsNodeChangeFileService extends IService<UmsNodeChangeFile> {

    void saveBatchs(List<UmsNodeChangeFile> fileList);

    List<UmsNodeChangeFile> selectListByNodeIds(List<Long> ids);

    void deleteByNodeIds(List<Long> exitChangeIds);
}
