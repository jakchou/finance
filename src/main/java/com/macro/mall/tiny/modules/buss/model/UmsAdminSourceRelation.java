package com.macro.mall.tiny.modules.buss.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 后台用户和资源关系表
 * </p>
 *
 * @author macro
 * @since 2024-03-30
 */
@Getter
@Setter
@TableName("ums_admin_source_relation")
@Schema(name = "UmsAdminSourceRelation", description = "后台用户和资源关系表")
public class UmsAdminSourceRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private Long resourceId;
}
