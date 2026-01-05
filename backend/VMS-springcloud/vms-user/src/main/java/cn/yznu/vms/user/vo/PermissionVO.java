package cn.yznu.vms.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 权限菜单响应VO（用于前端动态菜单）
 */
@Data
@Schema(description = "权限菜单VO")
public class PermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "类型: MENU/BUTTON")
    private String type;

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

    @Schema(description = "是否KeepAlive缓存")
    private Boolean keepAlive;

    @Schema(description = "布局模式")
    private String layout;

    @Schema(description = "重定向路径")
    private String redirect;

    @Schema(description = "子权限列表")
    private List<PermissionVO> children;
}
