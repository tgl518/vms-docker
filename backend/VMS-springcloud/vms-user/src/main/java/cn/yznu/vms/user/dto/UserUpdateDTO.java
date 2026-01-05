package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员更新用户请求
 */
@Data
@Schema(description = "管理员更新用户请求")
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", required = true)
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色:admin/user")
    private String role;

    @Schema(description = "状态:1正常 0禁用")
    private Integer status;
}
