package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新权限DTO
 */
@Data
@Schema(description = "更新权限请求")
public class PermissionUpdateDTO {

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "类型: MENU/BUTTON")
    private String type;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer order;

    @Schema(description = "是否显示")
    private Boolean show;

    @Schema(description = "是否启用")
    private Boolean enable;

    @Schema(description = "是否KeepAlive")
    private Boolean keepAlive;

    @Schema(description = "布局模式")
    private String layout;

    @Schema(description = "重定向路径")
    private String redirect;
}
