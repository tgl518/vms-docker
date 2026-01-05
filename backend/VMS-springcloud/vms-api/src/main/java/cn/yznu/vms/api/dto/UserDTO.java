package cn.yznu.vms.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户基本信息 DTO (用于 Feign 调用返回)
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
}
