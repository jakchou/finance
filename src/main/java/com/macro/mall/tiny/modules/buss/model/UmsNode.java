package com.macro.mall.tiny.modules.buss.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
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
@TableName("ums_node")
@Schema(name = "UmsNode", description = "节点对象")
public class UmsNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "关联id")
    private Long columnId;

    @Schema(description = "类型")
    private Integer categoryType;

}
