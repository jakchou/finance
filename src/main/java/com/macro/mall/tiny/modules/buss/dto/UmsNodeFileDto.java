package com.macro.mall.tiny.modules.buss.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 节点文件
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@Schema(name = "UmsNodeFile", description = "节点文件")
public class UmsNodeFileDto implements Serializable {

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

    @Schema(description = "是否删除")
    private Integer isDelete;
}
