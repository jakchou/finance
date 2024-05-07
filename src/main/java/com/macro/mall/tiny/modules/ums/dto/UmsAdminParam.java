package com.macro.mall.tiny.modules.ums.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

/**
 * 用户登录参数
 * Created by macro on 2018/4/26.
 */
@Data
public class UmsAdminParam {
    @NotEmpty
    @Schema(title = "用户名", required = true)
    private String username;
    @NotEmpty
    @Schema(title = "密码", required = true)
    private String password;
    @Schema(title = "用户头像")
    private String icon;

    @Schema(title = "邮箱")
    private String email;
    @Schema(title = "用户昵称")
    private String nickName;
    @Schema(title = "备注")
    private String note;

    @Schema(title = "角色")
    private Integer roleType;

    @Schema(title = "部门")
    private Long departmentId;

    @Schema(title = "分配")
    private Integer assign;
}
