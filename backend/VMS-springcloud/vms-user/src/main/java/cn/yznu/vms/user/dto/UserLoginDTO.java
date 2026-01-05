package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", required = true, example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", required = true, example = "admin123")
    @NotBlank(message = "密码不能为空")
    private String password;
}
