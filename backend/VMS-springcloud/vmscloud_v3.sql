-- ========================================
-- VMS-SpringCloud 数据库初始化脚本 V3.0 (完整版)
-- 数据库名: vms_cloud
-- 创建时间: 2025-12-20
-- ========================================
-- V3.0 新增内容:
--   1. RBAC权限系统 - 角色表、权限表、关联表
--   2. 系统配置表 - 动态配置管理
--   3. 移除sys_user的role字段，改用RBAC
-- ========================================

-- 创建数据库 (如果不存在)
CREATE
DATABASE IF NOT EXISTS vms_cloud
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE
vms_cloud;

-- 先删除所有表(按依赖顺序，从子表到父表)
DROP TABLE IF EXISTS watch_history;
DROP TABLE IF EXISTS user_like;
DROP TABLE IF EXISTS user_favorite;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS video_tag_rel;
DROP TABLE IF EXISTS video_episode;
DROP TABLE IF EXISTS video;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS banner;
DROP TABLE IF EXISTS sys_user_info;
-- RBAC相关表
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
-- 系统配置表
DROP TABLE IF EXISTS sys_config;
-- 用户表最后删
DROP TABLE IF EXISTS sys_user;

-- ========================================
-- 一、系统管理模块
-- ========================================

-- ----------------------------------------
-- 1. 用户核心表 (认证信息)
-- ----------------------------------------
CREATE TABLE sys_user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 1正常 0禁用',
    deleted     TINYINT      DEFAULT 0 COMMENT '软删除: 0未删 1已删',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username),
    KEY         idx_email (email),
    KEY         idx_status (status),
    KEY         idx_deleted (deleted)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- ----------------------------------------
-- 2. 用户档案表 (扩展信息)
-- ----------------------------------------
CREATE TABLE sys_user_info
(
    user_id     BIGINT PRIMARY KEY COMMENT '用户ID',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    avatar      VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    gender      TINYINT      DEFAULT 2 COMMENT '性别: 0女 1男 2保密',
    intro       VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    birthday    DATE         DEFAULT NULL COMMENT '生日',
    location    VARCHAR(100) DEFAULT NULL COMMENT '所在地',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_userinfo_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='用户档案表';

-- ----------------------------------------
-- 3. 角色表 (RBAC)
-- ----------------------------------------
CREATE TABLE sys_role
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code        VARCHAR(50)  NOT NULL COMMENT '角色编码(唯一)',
    name        VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    sort        INT          DEFAULT 0 COMMENT '排序',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    deleted     TINYINT      DEFAULT 0 COMMENT '软删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code),
    KEY         idx_status (status),
    KEY         idx_deleted (deleted)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='角色表';

-- ----------------------------------------
-- 4. 权限表 (RBAC)
-- ----------------------------------------
CREATE TABLE sys_permission
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    code        VARCHAR(100) NOT NULL COMMENT '权限编码(唯一)',
    name        VARCHAR(100) NOT NULL COMMENT '权限名称',
    type        VARCHAR(20)  DEFAULT 'MENU' COMMENT '类型: MENU菜单 BUTTON按钮 API接口',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父权限ID(0=顶级)',
    path        VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    component   VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    icon        VARCHAR(100) DEFAULT NULL COMMENT '图标',
    sort        INT          DEFAULT 0 COMMENT '排序',
    is_show     TINYINT      DEFAULT 1 COMMENT '是否显示: 1是 0否',
    is_enable   TINYINT      DEFAULT 1 COMMENT '是否启用: 1是 0否',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code),
    KEY         idx_parent (parent_id),
    KEY         idx_type (type),
    KEY         idx_sort (sort)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='权限表';

-- ----------------------------------------
-- 5. 用户角色关联表 (RBAC)
-- ----------------------------------------
CREATE TABLE sys_user_role
(
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    KEY     idx_role (role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='用户角色关联表';

-- ----------------------------------------
-- 6. 角色权限关联表 (RBAC)
-- ----------------------------------------
CREATE TABLE sys_role_permission
(
    role_id       BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    KEY           idx_permission (permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES sys_permission (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='角色权限关联表';

-- ----------------------------------------
-- 7. 系统配置表
-- ----------------------------------------
CREATE TABLE sys_config
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_key   VARCHAR(100) NOT NULL COMMENT '配置键(唯一)',
    config_value TEXT         DEFAULT NULL COMMENT '配置值',
    config_type  VARCHAR(20)  DEFAULT 'STRING' COMMENT '类型: STRING/NUMBER/BOOLEAN/JSON',
    description  VARCHAR(255) DEFAULT NULL COMMENT '配置描述',
    is_system    TINYINT      DEFAULT 0 COMMENT '系统内置: 1是 0否(系统内置不可删除)',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_key (config_key)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表';

-- ========================================
-- 二、视频模块
-- ========================================

-- ----------------------------------------
-- 8. 视频分类表
-- ----------------------------------------
CREATE TABLE category
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '分类名称',
    slug        VARCHAR(50)  DEFAULT NULL COMMENT '别名(英文URL)',
    icon        VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
    description VARCHAR(255) DEFAULT NULL COMMENT '描述',
    parent_id   BIGINT       DEFAULT 0 COMMENT '父分类ID(0=顶级)',
    sort        INT          DEFAULT 0 COMMENT '排序(小优先)',
    status      TINYINT      DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    deleted     TINYINT      DEFAULT 0 COMMENT '软删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_name (name),
    KEY         idx_parent (parent_id),
    KEY         idx_sort (sort),
    KEY         idx_deleted (deleted)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='分类表';

-- ----------------------------------------
-- 9. 标签表
-- ----------------------------------------
CREATE TABLE tag
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '标签名',
    color       VARCHAR(20) DEFAULT '#409EFF' COMMENT '颜色(HEX)',
    use_count   INT         DEFAULT 0 COMMENT '使用次数',
    deleted     TINYINT     DEFAULT 0 COMMENT '软删除',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_name (name),
    KEY         idx_use_count (use_count DESC),
    KEY         idx_deleted (deleted)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='标签表';

-- ----------------------------------------
-- 10. 视频主表
-- ----------------------------------------
CREATE TABLE video
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title          VARCHAR(200) NOT NULL COMMENT '标题',
    intro          TEXT         DEFAULT NULL COMMENT '简介',
    cover_url      VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    video_url      VARCHAR(500) DEFAULT NULL COMMENT '视频URL(单集)',
    category_id    BIGINT       DEFAULT NULL COMMENT '分类ID',
    user_id        BIGINT       NOT NULL COMMENT '上传者ID',
    status         TINYINT      DEFAULT 0 COMMENT '状态: 0草稿 1审核 2发布 3下架',
    view_count     INT UNSIGNED DEFAULT 0 COMMENT '播放量',
    like_count     INT UNSIGNED DEFAULT 0 COMMENT '点赞数',
    favorite_count INT UNSIGNED DEFAULT 0 COMMENT '收藏数',
    comment_count  INT UNSIGNED DEFAULT 0 COMMENT '评论数',
    duration       INT          DEFAULT 0 COMMENT '时长(秒)',
    is_vip         TINYINT      DEFAULT 0 COMMENT 'VIP: 0否 1是',
    publish_time   DATETIME     DEFAULT NULL COMMENT '发布时间',
    deleted        TINYINT      DEFAULT 0 COMMENT '软删除',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY            idx_category (category_id),
    KEY            idx_user (user_id),
    KEY            idx_status (status),
    KEY            idx_view (view_count DESC),
    KEY            idx_create (create_time DESC),
    KEY            idx_deleted (deleted),
    FULLTEXT KEY ft_title (title),
    CONSTRAINT fk_video_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    CONSTRAINT fk_video_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='视频表';

-- ----------------------------------------
-- 11. 视频分集表
-- ----------------------------------------
CREATE TABLE video_episode
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    video_id    BIGINT       NOT NULL COMMENT '视频ID',
    title       VARCHAR(100) DEFAULT NULL COMMENT '分集标题',
    file_url    VARCHAR(500) NOT NULL COMMENT '文件URL',
    cover_url   VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    duration    INT          DEFAULT 0 COMMENT '时长(秒)',
    sort        INT          DEFAULT 0 COMMENT '排序',
    is_free     TINYINT      DEFAULT 1 COMMENT '免费: 1是 0否',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY         idx_video (video_id),
    KEY         idx_sort (sort),
    CONSTRAINT fk_episode_video FOREIGN KEY (video_id) REFERENCES video (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='视频分集表';

-- ----------------------------------------
-- 12. 视频标签关联表
-- ----------------------------------------
CREATE TABLE video_tag_rel
(
    video_id BIGINT NOT NULL COMMENT '视频ID',
    tag_id   BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (video_id, tag_id),
    KEY      idx_tag (tag_id),
    CONSTRAINT fk_rel_video FOREIGN KEY (video_id) REFERENCES video (id) ON DELETE CASCADE,
    CONSTRAINT fk_rel_tag FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='视频标签关联';

-- ========================================
-- 三、互动模块
-- ========================================

-- ----------------------------------------
-- 13. 评论表
-- ----------------------------------------
CREATE TABLE comment
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    video_id      BIGINT NOT NULL COMMENT '视频ID',
    user_id       BIGINT NOT NULL COMMENT '用户ID',
    parent_id     BIGINT   DEFAULT 0 COMMENT '父评论ID(0=顶级)',
    reply_user_id BIGINT   DEFAULT NULL COMMENT '回复目标用户ID',
    content       TEXT   NOT NULL COMMENT '内容',
    like_count    INT UNSIGNED DEFAULT 0 COMMENT '点赞数',
    status        TINYINT  DEFAULT 1 COMMENT '状态: 1正常 0隐藏',
    deleted       TINYINT  DEFAULT 0 COMMENT '软删除',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY           idx_video (video_id),
    KEY           idx_user (user_id),
    KEY           idx_parent (parent_id),
    KEY           idx_create (create_time DESC),
    KEY           idx_deleted (deleted),
    CONSTRAINT fk_comment_video FOREIGN KEY (video_id) REFERENCES video (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='评论表';

-- ----------------------------------------
-- 14. 用户收藏表
-- ----------------------------------------
CREATE TABLE user_favorite
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    video_id    BIGINT NOT NULL COMMENT '视频ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_user_video (user_id, video_id),
    KEY         idx_video (video_id),
    CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_video FOREIGN KEY (video_id) REFERENCES video (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='收藏表';

-- ----------------------------------------
-- 15. 用户点赞表
-- ----------------------------------------
CREATE TABLE user_like
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id     BIGINT  NOT NULL COMMENT '用户ID',
    target_type TINYINT NOT NULL COMMENT '类型: 1视频 2评论',
    target_id   BIGINT  NOT NULL COMMENT '目标ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    KEY         idx_target (target_type, target_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='点赞表';

-- ----------------------------------------
-- 16. 观看历史表
-- ----------------------------------------
CREATE TABLE watch_history
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    video_id        BIGINT NOT NULL COMMENT '视频ID',
    episode_id      BIGINT   DEFAULT NULL COMMENT '分集ID',
    watch_duration  INT      DEFAULT 0 COMMENT '观看时长(秒)',
    watch_progress  INT      DEFAULT 0 COMMENT '观看进度(秒)',
    last_watch_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后观看时间',
    UNIQUE KEY uk_user_video (user_id, video_id),
    KEY             idx_user (user_id),
    KEY             idx_time (last_watch_time DESC),
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_history_video FOREIGN KEY (video_id) REFERENCES video (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='观看历史表';

-- ========================================
-- 四、运营模块
-- ========================================

-- ----------------------------------------
-- 17. 轮播图表
-- ----------------------------------------
CREATE TABLE banner
(
    id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title       VARCHAR(100) DEFAULT NULL COMMENT '标题',
    img_url     VARCHAR(500) NOT NULL COMMENT '图片URL',
    link_url    VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    link_type   TINYINT      DEFAULT 0 COMMENT '类型: 0外链 1视频 2分类',
    target_id   BIGINT       DEFAULT NULL COMMENT '目标ID',
    sort        INT          DEFAULT 0 COMMENT '排序',
    is_show     TINYINT      DEFAULT 1 COMMENT '显示: 1是 0否',
    start_time  DATETIME     DEFAULT NULL COMMENT '开始时间',
    end_time    DATETIME     DEFAULT NULL COMMENT '结束时间',
    deleted     TINYINT      DEFAULT 0 COMMENT '软删除',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY         idx_sort (sort),
    KEY         idx_show (is_show),
    KEY         idx_deleted (deleted)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT ='轮播图表';

-- ========================================
-- 初始数据
-- ========================================

-- ----------------------------------------
-- 角色数据
-- ----------------------------------------
INSERT INTO sys_role (id, code, name, description, sort, status)
VALUES (1, 'admin', '超级管理员', '拥有系统所有权限', 1, 1),
       (2, 'user', '普通用户', '普通注册用户', 10, 1);

-- ----------------------------------------
-- 权限数据 (菜单 + 按钮)
-- ----------------------------------------
INSERT INTO sys_permission (id, code, name, type, parent_id, path, component, icon, sort, is_show, is_enable)
VALUES
-- 顶级菜单
(1, 'Dashboard', '仪表盘', 'MENU', 0, '/admin/dashboard', '/src/views/vms/dashboard/index.vue', 'i-fe:bar-chart-2', 0, 1, 1),
(2, 'UserMgt', '用户管理', 'MENU', 0, '/admin/pms/user', '/src/views/pms/user/index.vue', 'i-fe:user', 3, 1, 1),
(3, 'VideoMgt', '视频管理', 'MENU', 0, NULL, NULL, 'i-fe:video', 1, 1, 1),
-- 视频管理子菜单
(31, 'VideoList', '视频列表', 'MENU', 3, '/admin/vms/video', '/src/views/vms/video/index.vue', 'i-fe:list', 1, 1, 1),
(32, 'CategoryList', '分类管理', 'MENU', 3, '/admin/vms/category', '/src/views/vms/category/index.vue', 'i-fe:grid', 2, 1, 1),
(33, 'TagList', '标签管理', 'MENU', 3, '/admin/vms/tag', '/src/views/vms/tag/index.vue', 'i-fe:tag', 3, 1, 1),
(34, 'BannerList', '轮播图管理', 'MENU', 3, '/admin/vms/banner', '/src/views/vms/banner/index.vue', 'i-fe:image', 4, 1, 1),
(35, 'CommentList', '评论管理', 'MENU', 3, '/admin/vms/comment', '/src/views/vms/comment/index.vue', 'i-fe:message-square', 5, 1, 1),
-- 系统管理菜单
(4, 'SystemMgt', '系统管理', 'MENU', 0, NULL, NULL, 'i-fe:settings', 10, 1, 1),
(41, 'RoleMgt', '角色管理', 'MENU', 4, '/admin/pms/role', '/src/views/pms/role/index.vue', 'i-fe:shield', 1, 1, 1),
(42, 'PermissionMgt', '权限管理', 'MENU', 4, '/admin/pms/resource', '/src/views/pms/resource/index.vue', 'i-fe:lock', 2, 1, 1),
(43, 'ConfigMgt', '配置管理', 'MENU', 4, '/admin/pms/config', '/src/views/pms/config/index.vue', 'i-fe:sliders', 3, 1, 1),
-- 按钮权限
(100, 'user:add', '新增用户', 'BUTTON', 2, NULL, NULL, NULL, 1, 0, 1),
(101, 'user:edit', '编辑用户', 'BUTTON', 2, NULL, NULL, NULL, 2, 0, 1),
(102, 'user:delete', '删除用户', 'BUTTON', 2, NULL, NULL, NULL, 3, 0, 1),
(103, 'video:add', '新增视频', 'BUTTON', 31, NULL, NULL, NULL, 1, 0, 1),
(104, 'video:edit', '编辑视频', 'BUTTON', 31, NULL, NULL, NULL, 2, 0, 1),
(105, 'video:delete', '删除视频', 'BUTTON', 31, NULL, NULL, NULL, 3, 0, 1);

-- ----------------------------------------
-- 角色权限关联 (管理员拥有所有权限)
-- ----------------------------------------
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 31),
       (1, 32),
       (1, 33),
       (1, 34),
       (1, 35),
       (1, 4),
       (1, 41),
       (1, 42),
       (1, 43),
       (1, 100),
       (1, 101),
       (1, 102),
       (1, 103),
       (1, 104),
       (1, 105);

-- 普通用户只有视频列表查看权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES (2, 3),
       (2, 31);

-- ----------------------------------------
-- 管理员用户 (密码: 123456)
-- ----------------------------------------
INSERT INTO sys_user (id, username, password, status)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1);

INSERT INTO sys_user_info (user_id, nickname, avatar, gender, intro)
VALUES (1, '系统管理员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 2, 'VMS视频管理系统管理员');

INSERT INTO sys_user_role (user_id, role_id)
VALUES (1, 1);

-- ----------------------------------------
-- 测试用户 (密码: 123456)
-- ----------------------------------------
INSERT INTO sys_user (id, username, password, status)
VALUES (2, 'test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1);

INSERT INTO sys_user_info (user_id, nickname, avatar, gender, intro)
VALUES (2, '测试用户', 'https://api.dicebear.com/7.x/avataaars/svg?seed=test', 1, '这个人很懒，什么都没写~');

INSERT INTO sys_user_role (user_id, role_id)
VALUES (2, 2);

-- ----------------------------------------
-- 系统配置数据
-- ----------------------------------------
INSERT INTO sys_config (config_key, config_value, config_type, description, is_system)
VALUES ('site_name', 'VMS视频管理系统', 'STRING', '站点名称', 1),
       ('site_logo', '/logo.png', 'STRING', '站点Logo', 1),
       ('upload_max_size', '104857600', 'NUMBER', '最大上传大小(字节,默认100MB)', 1),
       ('allowed_video_types', 'mp4,webm,mkv,avi,mov', 'STRING', '允许的视频格式', 1),
       ('allowed_image_types', 'jpg,jpeg,png,gif,webp', 'STRING', '允许的图片格式', 1),
       ('video_default_status', '0', 'NUMBER', '视频默认状态(0草稿)', 1),
       ('comment_need_audit', 'false', 'BOOLEAN', '评论是否需要审核', 0),
       ('register_enabled', 'true', 'BOOLEAN', '是否开放注册', 0);

-- ----------------------------------------
-- 分类数据
-- ----------------------------------------
INSERT INTO category (name, slug, sort, status)
VALUES ('电影', 'movie', 1, 1),
       ('电视剧', 'tv', 2, 1),
       ('动漫', 'anime', 3, 1),
       ('综艺', 'variety', 4, 1),
       ('纪录片', 'documentary', 5, 1),
       ('游戏', 'game', 6, 1),
       ('音乐', 'music', 7, 1),
       ('短视频', 'short', 8, 1);

-- ----------------------------------------
-- 标签数据
-- ----------------------------------------
INSERT INTO tag (name, color, use_count)
VALUES ('热门', '#F56C6C', 100),
       ('推荐', '#E6A23C', 80),
       ('新上线', '#67C23A', 60),
       ('经典', '#409EFF', 50),
       ('高分', '#FB7299', 40),
       ('国产', '#909399', 30),
       ('欧美', '#2196F3', 25),
       ('日韩', '#9C27B0', 20);

-- ----------------------------------------
-- 轮播图数据
-- ----------------------------------------
INSERT INTO banner (title, img_url, link_type, sort, is_show)
VALUES ('欢迎使用VMS', 'https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=1200', 0, 1, 1),
       ('探索精彩视频', 'https://images.unsplash.com/photo-1485846234645-a62644f84728?w=1200', 0, 2, 1);

-- ========================================
-- 完成提示
-- ========================================
SELECT '========================================' AS '';
SELECT 'VMS数据库 V3.0 初始化完成!' AS message;
SELECT '========================================' AS '';
SELECT CONCAT('用户数: ', COUNT(*)) AS info
FROM sys_user;
SELECT CONCAT('角色数: ', COUNT(*)) AS info
FROM sys_role;
SELECT CONCAT('权限数: ', COUNT(*)) AS info
FROM sys_permission;
SELECT CONCAT('配置项: ', COUNT(*)) AS info
FROM sys_config;
SELECT CONCAT('分类数: ', COUNT(*)) AS info
FROM category;
SELECT CONCAT('标签数: ', COUNT(*)) AS info
FROM tag;
