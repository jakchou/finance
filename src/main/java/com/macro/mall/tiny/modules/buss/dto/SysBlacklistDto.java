package com.macro.mall.tiny.modules.buss.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * toekn黑名单
 * </p>
 *
 * @author macro
 * @since 2024-03-28
 */
@Getter
@Setter
@Schema(name = "SysBlacklist", description = "toekn黑名单")
public class SysBlacklistDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @Schema(description = "token")
    private String token;
}
