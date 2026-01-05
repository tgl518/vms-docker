package cn.yznu.vms.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 当前登录用户信息
 * 从 Token 解析后存储在请求上下文中
 */
@Data
@Schema(description = "当前登录用户信息")
public class CurrentUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 角色
     */
    @Schema(description = "角色: admin/user")
    private String role;

    /**
     * 判断是否是管理员
     */
    public boolean isAdmin() {
        return "admin".equals(role);
    }

    /**
     * 创建当前用户对象
     */
    public static CurrentUser of(Long userId, String username, String role) {
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(userId);
        currentUser.setUsername(username);
        currentUser.setRole(role);
        return currentUser;
    }
}
