package com.macro.mall.tiny.modules.buss.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@Schema(name = "SysOperLog", description = "")
public class SysOperLogDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "操作用户")
    private Long userId;

    @Schema(description = "模块名称")
    private String title;

    @Schema(description = "业务类型（1、新增 2、修改 3、删除 0 其他）")
    private Integer businessType;

    @Schema(description = "方法名称")
    private String uri;

    @Schema(description = "操作状态")
    private Integer status;

    @Schema(description = "入参")
    private String optParam;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "操作时间")
    private Date operTime;
}
