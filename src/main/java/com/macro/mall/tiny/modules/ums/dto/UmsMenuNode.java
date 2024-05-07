package com.macro.mall.tiny.modules.ums.dto;

import com.macro.mall.tiny.modules.ums.model.UmsMenu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 后台菜单节点封装
 *
 * @author macro
 * @date 2020/2/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UmsMenuNode extends UmsMenu {
    @Schema(title = "子级菜单")
    private List<UmsMenuNode> children;
}
