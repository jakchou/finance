package com.macro.mall.tiny.modules.buss.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 菜单关联节点表
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@Schema(name = "UmsColumn", description = "菜单关联节点表")
public class UmsColumnDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<UmsColumnDto> columnDtoList;

    private List<UmsNodeDto> nodeDtoList;

    private Long id;

    @Schema(description = "banner")
    private String banner;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "菜单id")
    private Long menuId;

    /*@Schema(description = "创建时间")
    private Date createTime;*/

    @Schema(description = "名称")
    private String name;

 /*   @Schema(description = "描述")
    private String description;

    @Schema(description = "内容")
    private String content;*/

    @Schema(description = "排序")
    private Integer sort;

   @Schema(description = "内容类型")
    private Integer categoryType;
/*
    @Schema(description = "url")
    private String url;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "隐藏")
    private Integer hidden;*/
}
