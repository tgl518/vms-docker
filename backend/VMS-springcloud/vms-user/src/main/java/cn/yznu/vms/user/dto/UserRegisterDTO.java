package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 */
@Data
@Schema(description = "用户注册请求")
@PasswordMatches
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名, 3-20位，支持字母、数字、下划线", required = true, example = "new_user_123")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "密码, 8-30位，必须包含大小写字母、数字和特殊字符", required = true, example = "Password123!")
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 30, message = "密码长度必须在8-30之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$", message = "密码必须包含大小写字母、数字和特殊字符")
    private String password;

    @Schema(description = "确认密码", required = true, example = "Password123!")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Schema(description = "昵称", example = "用户昵称")
    @Size(max = 30, message = "昵称长度不能超过30个字符")
    private String nickname;
}
