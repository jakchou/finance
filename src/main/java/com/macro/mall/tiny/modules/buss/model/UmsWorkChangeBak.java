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
 * 招聘变更表
 * </p>
 *
 * @author macro
 * @since 2024-03-29
 */
@Getter
@Setter
@TableName("ums_work_change_bak")
@Schema(name = "UmsWorkChangeBak", description = "招聘变更表")
public class UmsWorkChangeBak implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "work表主键")
    private Long workId;

    @Schema(description = "提交id")
    private Long submitId;

    @Schema(description = "名称")
    private String workName;

    @Schema(description = "人数")
    private String workCount;

    @Schema(description = "工作类型，1社招，2校招")
    private Integer workType;

    @Schema(description = "部门")
    private String department;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "工作经验")
    private String workExperience;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "工资")
    private String salary;

    @Schema(description = "详情")
    private String detail;

    @Schema(description = "创建更人")
    private Long createPerson;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "审核状态")
    private Integer status;
}
