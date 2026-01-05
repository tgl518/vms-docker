package cn.yznu.vms.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求参数
 */
@Data
@Schema(description = "用户查询请求参数")
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名(模糊搜索)")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "状态: 1正常 0禁用")
    private Integer status;

    @Schema(description = "性别: 0女 1男 2保密")
    private Byte gender;
}
