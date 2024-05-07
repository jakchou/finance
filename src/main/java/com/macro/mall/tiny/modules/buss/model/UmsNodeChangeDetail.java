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
 * 节点对象变更表
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@TableName("ums_node_change_detail")
@Schema(name = "UmsNodeChangeDetail", description = "节点对象变更表")
public class UmsNodeChangeDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "提交id")
    private Long submitId;

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

    @Schema(description = "nodeid")
    private Long nodeId;

    @Schema(description = "变更类型")
    private Integer changeType;

    @Schema(description = "类型")
    private Integer categoryType;

    @Schema(description = "审核状态")
    private Integer status;

    private Integer processStatus;
}
