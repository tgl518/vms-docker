package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 重置密码请求
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    private String password;
}
