package cn.yznu.vms.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录成功响应
 */
@Data
@Schema(description = "登录成功响应")
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "角色列表")
    private java.util.List<String> roles;

    @Schema(description = "权限列表")
    private java.util.List<String> permissions;

    @Schema(description = "JWT Token")
    private String accessToken;
}
