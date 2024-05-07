package com.macro.mall.tiny.modules.buss.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
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
@Schema(name = "UmsContract", description = "")
public class UmsContractDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String email;
    private String phone;
}
