package cn.yznu.vms.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 统一管理所有业务状态码，方便维护和国际化
 */
@Getter
public enum ResultCode {

    // ==================== 成功 (0) ====================
    SUCCESS(0, "OK"),

    // ==================== 客户端错误 (4xx) ====================
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // ==================== 业务错误 (5xx) ====================
    FAIL(500, "操作失败"),
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),

    // ==================== 用户相关 (1xxx) ====================
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "账号已被禁用"),
    TOKEN_INVALID(1005, "Token 无效或已过期"),
    TOKEN_EXPIRED(1006, "Token 已过期"),

    // ==================== 视频相关 (2xxx) ====================
    VIDEO_NOT_FOUND(2001, "视频不存在"),
    VIDEO_ALREADY_EXISTS(2002, "视频标题已存在"),
    CATEGORY_NOT_FOUND(2003, "分类不存在"),
    TAG_NOT_FOUND(2004, "标签不存在"),
    EPISODE_NOT_FOUND(2005, "分集不存在"),

    // ==================== 文件相关 (3xxx) ====================
    FILE_UPLOAD_ERROR(3001, "文件上传失败"),
    FILE_NOT_FOUND(3002, "文件不存在"),
    FILE_TYPE_NOT_ALLOWED(3003, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(3004, "文件大小超出限制"),

    // ==================== 互动相关 (4xxx) ====================
    COMMENT_NOT_FOUND(4001, "评论不存在"),
    ALREADY_LIKED(4002, "已经点赞过了"),
    ALREADY_FAVORITED(4003, "已经收藏过了");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 提示消息
     */
    private final String message;

    /**
     * 枚举构造函数（必须手动定义，Lombok 不支持枚举）
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
