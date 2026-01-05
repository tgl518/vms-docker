package cn.yznu.vms.user.entity;

import cn.yznu.vms.common.enums.StatusEnum;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户核心表
 */
@Data
@TableName("sys_user")
@Schema(name = "User", description = "用户核心表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "密码(BCrypt加密)")
    @TableField("password")
    private String password;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "状态: 1正常 0禁用")
    @TableField("status")
    private StatusEnum status;

    @Schema(description = "注册时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "软删除: 0未删 1已删")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

