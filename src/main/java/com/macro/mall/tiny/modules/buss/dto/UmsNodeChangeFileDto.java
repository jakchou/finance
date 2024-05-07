package com.macro.mall.tiny.modules.buss.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 变更节点文件
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@Schema(name = "UmsNodeChangeFile", description = "变更节点文件")
public class UmsNodeChangeFileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "名称")
    private String fileUrl;

    @Schema(description = "图片标题")
    private String fileTitle;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "nodeid")
    private Long nodeId;
}
