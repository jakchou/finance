package com.macro.mall.tiny.modules.ums.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 后台资源表
 * </p>
 *
 * @author macro
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title ="UmsResourceVo对象", description="后台资源表")
public class UmsResourceVo implements Serializable {

    private static final long serialVersionUID=1L;

    private List<UmsResourceVo> childList;

    private Long id;

    @Schema(title = "资源名称")
    private String name;

    @Schema(title = "关联节点")
    private Long relationId;

    @Schema(title = "资源分类")
    private Integer level;


}
