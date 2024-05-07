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
 * 字典类型表
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@TableName("base_dict")
@Schema(name = "BaseDict", description = "字典类型表")
public class BaseDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "字典编码")
    private String dictCode;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "字典值")
    private String dictValue;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典排序")
    private Integer dictSort;

    @Schema(description = "状态（0正常 1停用）")
    private String status;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "备注")
    private String remark;
}
