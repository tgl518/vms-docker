package cn.yznu.vms.common.constant;

/**
 * 系统常量定义
 */
public class Constants {

    private Constants() {
        // 工具类禁止实例化
    }

    // ==================== 用户角色 ====================
    /**
     * 超级管理员角色
     */
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * 管理员角色
     */
    public static final String ROLE_ADMIN = "admin";

    /**
     * 普通用户角色
     */
    public static final String ROLE_USER = "user";

    // ==================== 状态常量 ====================
    /**
     * 启用状态
     */
    public static final Byte STATUS_ENABLED = 1;

    /**
     * 禁用状态
     */
    public static final Byte STATUS_DISABLED = 0;

    // ==================== 视频状态 ====================
    /**
     * 草稿
     */
    public static final Byte VIDEO_STATUS_DRAFT = 0;

    /**
     * 审核中
     */
    public static final Byte VIDEO_STATUS_PENDING = 1;

    /**
     * 已发布
     */
    public static final Byte VIDEO_STATUS_PUBLISHED = 2;

    /**
     * 已下架
     */
    public static final Byte VIDEO_STATUS_OFFLINE = 3;

    // ==================== 性别 ====================
    /**
     * 女
     */
    public static final Byte GENDER_FEMALE = 0;

    /**
     * 男
     */
    public static final Byte GENDER_MALE = 1;

    /**
     * 保密
     */
    public static final Byte GENDER_SECRET = 2;

    // ==================== 点赞目标类型 ====================
    /**
     * 视频
     */
    public static final Byte LIKE_TARGET_VIDEO = 1;

    /**
     * 评论
     */
    public static final Byte LIKE_TARGET_COMMENT = 2;

    // ==================== 分页默认值 ====================
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页数量
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页数量
     */
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== 缓存 Key 前缀 ====================
    /**
     * 用户信息缓存前缀
     */
    public static final String CACHE_USER_PREFIX = "vms:user:";

    /**
     * 视频信息缓存前缀
     */
    public static final String CACHE_VIDEO_PREFIX = "vms:video:";

    /**
     * 分类缓存 Key
     */
    public static final String CACHE_CATEGORY_LIST = "vms:category:list";

    /**
     * 标签缓存 Key
     */
    public static final String CACHE_TAG_LIST = "vms:tag:list";

    /**
     * 轮播图缓存 Key
     */
    public static final String CACHE_BANNER_LIST = "vms:banner:list";
}
