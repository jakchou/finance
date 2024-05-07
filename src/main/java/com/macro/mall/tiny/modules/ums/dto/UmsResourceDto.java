package com.macro.mall.tiny.modules.ums.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.macro.mall.tiny.modules.ums.model.UmsResource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Schema(title ="UmsResource对象", description="后台资源表")
public class UmsResourceDto implements Serializable {

    private static final long serialVersionUID=1L;

    private List<UmsResourceDto> childList;

    private Long id;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "资源名称")
    private String name;

    @Schema(title = "资源URL")
    private String url;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "资源分类")
    private Long categoryType;

    @Schema(title = "关联节点")
    private Long relationId;

    @Schema(title = "父节点")
    private Long parentId;

    @Schema(title = "资源分类")
    private Integer level;

    @Schema(title = "隐藏")
    private Integer hide;


}
