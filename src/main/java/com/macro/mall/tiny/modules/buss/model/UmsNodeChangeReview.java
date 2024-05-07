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
 * 节点对象变更审核
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@TableName("ums_node_change_review")
@Schema(name = "UmsNodeChangeReview", description = "节点对象变更审核")
public class UmsNodeChangeReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "提交id")
    private Long submitId;

    @Schema(description = "审核人")
    private Long createPerson;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "审核内容")
    private String content;

    @Schema(description = "审核结果")
    private Integer status;
}
