package com.macro.mall.tiny.modules.buss.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 招聘表
 * </p>
 *
 * @author macro
 * @since 2024-03-29
 */
@Getter
@Setter
@TableName("ums_work")
@Schema(name = "UmsWork", description = "招聘表")
public class UmsWork implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "名称")
    private String workName;

    @Schema(description = "人数")
    private String workCount;

    @Schema(description = "工作类型，1社招，2校招")
    private Integer workType;

    @TableField(exist = false)
    private String workTypeName;

    @Schema(description = "部门")
    @TableField(exist = false)
    private String departmentName;

    private Long departmentId;

    @Schema(description = "联系人")
    @TableField(exist = false)
    private String contactName;


    private Long contactId;

    @Schema(description = "工作经验")
    private String workExperience;

    @Schema(description = "学历")
    private Integer education;
    @TableField(exist = false)
    private String educationName;

    @Schema(description = "工资")
    private String salary;

    @Schema(description = "详情")
    private String detail;

    @Schema(description = "创建更人")
    private Long createPerson;

    @Schema(description = "创建时间")
    private Date createTime;

}
