package com.macro.mall.tiny.modules.buss.model;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author macro
 * @since 2024-04-02
 */
@Getter
@Setter
@TableName("ums_department")
@Schema(name = "UmsDepartment", description = "")
public class UmsDepartment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long parentId;

    private Long level;
}
