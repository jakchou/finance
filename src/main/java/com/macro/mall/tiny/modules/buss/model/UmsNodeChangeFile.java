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
 * 变更节点文件
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@TableName("ums_node_change_file")
@Schema(name = "UmsNodeChangeFile", description = "变更节点文件")
public class UmsNodeChangeFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "url")
    private String fileUrl;

    private String fileTitle;

    @Schema(description = "名称")
    private String fileName;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "nodeid")
    private Long nodeId;
}
