package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员创建用户请求
 */
@Data
@Schema(description = "管理员创建用户请求")
public class UserCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色:admin/user", example = "user")
    private String role;

    @Schema(description = "状态:1正常 0禁用", example = "1")
    private Integer status;
}
