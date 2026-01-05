package cn.yznu.vms.user.vo;

import cn.yznu.vms.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户详情响应
 */
@Data
@Schema(description = "用户详情响应")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色列表")
    private java.util.List<String> roles;

    @Schema(description = "权限列表")
    private java.util.List<String> permissions;

    @Schema(description = "账号状态: 1正常 0禁用")
    private StatusEnum status;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别: 0女 1男 2保密")
    private Byte gender;

    @Schema(description = "个人简介")
    private String intro;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "所在地")
    private String location;

    @Schema(description = "注册时间")
    private LocalDateTime createTime;
}

