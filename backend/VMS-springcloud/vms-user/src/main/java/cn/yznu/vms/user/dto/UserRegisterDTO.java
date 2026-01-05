package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", required = true, example = "newuser")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    private String username;

    @Schema(description = "密码", required = true, example = "password123")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50之间")
    private String password;

    @Schema(description = "确认密码", required = true, example = "password123")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Schema(description = "昵称", example = "用户昵称")
    private String nickname;
}
