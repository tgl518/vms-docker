package cn.yznu.vms.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联表 (RBAC)
 */
@Data
@TableName("sys_user_role")
@Schema(name = "UserRole", description = "用户角色关联表")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "角色ID")
    @TableField("role_id")
    private Long roleId;
}
