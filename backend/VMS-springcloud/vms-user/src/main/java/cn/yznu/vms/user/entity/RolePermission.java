package cn.yznu.vms.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限关联表 (RBAC)
 */
@Data
@TableName("sys_role_permission")
@Schema(name = "RolePermission", description = "角色权限关联表")
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    @TableField("role_id")
    private Long roleId;

    @Schema(description = "权限ID")
    @TableField("permission_id")
    private Long permissionId;
}
