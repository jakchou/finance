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
 * 节点对象
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@Schema(name = "UmsNode", description = "节点对象")
public class UmsNodeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private List<UmsNodeFileDto> fileDtoList;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "属性")
    private String attributes;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "变更人")
    private Long createPerson;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "colmnid")
    private Long columnId;


    @Schema(description = "类型")
    private Integer categoryType;

    @Schema(description = "审核状态")
    private Integer status;
}
