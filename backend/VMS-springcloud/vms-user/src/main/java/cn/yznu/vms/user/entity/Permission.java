package cn.yznu.vms.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限表 (RBAC)
 */
@Data
@TableName("sys_permission")
@Schema(name = "Permission", description = "权限表")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "权限编码(唯一)")
    @TableField("code")
    private String code;

    @Schema(description = "权限名称")
    @TableField("name")
    private String name;

    @Schema(description = "类型: MENU菜单 BUTTON按钮 API接口")
    @TableField("type")
    private String type;

    @Schema(description = "父权限ID(0=顶级)")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "路由路径")
    @TableField("path")
    private String path;

    @Schema(description = "组件路径")
    @TableField("component")
    private String component;

    @Schema(description = "图标")
    @TableField("icon")
    private String icon;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "是否显示: 1是 0否")
    @TableField("is_show")
    private Integer isShow;

    @Schema(description = "是否启用: 1是 0否")
    @TableField("is_enable")
    private Integer isEnable;

    @Schema(description = "是否KeepAlive缓存: 1是 0否")
    @TableField("keep_alive")
    private Integer keepAlive;

    @Schema(description = "布局模式")
    @TableField("layout")
    private String layout;

    @Schema(description = "重定向路径")
    @TableField("redirect")
    private String redirect;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
