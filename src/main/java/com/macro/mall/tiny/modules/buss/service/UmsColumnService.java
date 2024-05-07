package com.macro.mall.tiny.modules.buss.service;

import com.macro.mall.tiny.modules.buss.dto.*;
import com.macro.mall.tiny.modules.buss.model.UmsColumn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单关联节点表 服务类
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
public interface UmsColumnService extends IService<UmsColumn> {

    Long updateSource(UmsColumnChangeDto colum);

    void reviewSource(UmsNodeChangeReviewDto reviewDto);

    Long saveNewSource(UmsNodeChangeDetailDto detailDto);

    void deleteSource(UmsNodeDto nodeDto);

    Long updateNewSource(UmsNodeChangeDetailDto detailDto);

    void saveJobSource(UmsWorkChangeDto workDto);

    void updateJobSource(UmsWorkChangeDto workDto);

    void deleteJobSource(UmsWorkDto workDto);

    UmsColumnDto querySource(Long menuId, Long columnId);

    List<UmsColumnDto> queryColumn(Long menuId);

    List<UmsNodeDto> newSourceList(Long menuId,Long columnId);

    void deleteNewsSource(UmsNodeDto job);

    List<UmsColumnChangeDto> reviewSourceList();

    UmsColumnChangeDto reviewSourceDetail(Long id);

    UmsNodeDto newSourceDetail(Long columnId,Long nodeId);

    List<UmsWorkDto> jobList();

    UmsWorkDto jobDetail(Long id);
}
