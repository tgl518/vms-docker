-- ========================================
-- VMS-Cloud 数据库初始化脚本 (动漫版 v3)
-- 包含: 表结构 + 丰富动漫演示数据
-- 视频使用可播放的MP4链接
-- 创建时间: 2026-01-06
-- ========================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `vms_cloud` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `vms_cloud`;

-- ========================================
-- 一、系统管理模块
-- ========================================

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_user` VALUES 
(1,'admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',NULL,NULL,1,0,NOW(),NOW()),
(2,'test','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','test@test.com',NULL,1,0,NOW(),NOW());

DROP TABLE IF EXISTS `sys_user_info`;
CREATE TABLE `sys_user_info` (
  `user_id` bigint NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `avatar` varchar(500) DEFAULT NULL,
  `gender` tinyint DEFAULT '2',
  `intro` varchar(500) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_userinfo_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_user_info` VALUES 
(1,'系统管理员','/media/images/default/cover.png',1,'VMS动漫站管理员',NULL,NULL,NOW(),NOW()),
(2,'动漫迷','/media/images/default/cover.png',1,'二次元爱好者~',NULL,NULL,NOW(),NOW());

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `sort` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_role` VALUES 
(1,'SUPER_ADMIN','超级管理员','拥有所有权限',0,1,0,NOW(),NOW()),
(2,'user','普通用户','普通用户',2,1,0,NOW(),NOW()),
(3,'admin','管理员','管理员',1,1,0,NOW(),NOW());

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` varchar(20) DEFAULT 'MENU',
  `parent_id` bigint DEFAULT '0',
  `path` varchar(200) DEFAULT NULL,
  `component` varchar(200) DEFAULT NULL,
  `icon` varchar(100) DEFAULT NULL,
  `sort` int DEFAULT '0',
  `is_show` tinyint DEFAULT '1',
  `is_enable` tinyint DEFAULT '1',
  `keep_alive` tinyint DEFAULT '0',
  `layout` varchar(50) DEFAULT NULL,
  `redirect` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_permission` VALUES 
(1,'Dashboard','仪表盘','MENU',0,'/admin/dashboard','/src/views/vms/dashboard/index.vue','i-fe:bar-chart-2',0,1,1,0,NULL,NULL,NOW(),NOW()),
(2,'UserMgt','用户管理','MENU',0,'/admin/pms/user','/src/views/pms/user/index.vue','i-fe:user',3,1,1,0,NULL,NULL,NOW(),NOW()),
(3,'VideoMgt','视频管理','MENU',0,NULL,NULL,'i-fe:video',2,1,1,0,NULL,NULL,NOW(),NOW()),
(4,'SystemMgt','系统管理','MENU',0,NULL,NULL,'i-fe:settings',10,1,1,0,NULL,NULL,NOW(),NOW()),
(31,'VideoList','视频列表','MENU',3,'/admin/vms/video','/src/views/vms/video/index.vue','i-fe:list',1,1,1,0,NULL,NULL,NOW(),NOW()),
(32,'CategoryList','分类管理','MENU',3,'/admin/vms/category','/src/views/vms/category/index.vue','i-fe:grid',2,1,1,0,NULL,NULL,NOW(),NOW()),
(33,'TagList','标签管理','MENU',3,'/admin/vms/tag','/src/views/vms/tag/index.vue','i-fe:tag',3,1,1,0,NULL,NULL,NOW(),NOW()),
(34,'BannerList','轮播图管理','MENU',3,'/admin/vms/banner','/src/views/vms/banner/index.vue','i-fe:image',4,1,1,0,NULL,NULL,NOW(),NOW()),
(35,'CommentList','评论管理','MENU',3,'/admin/vms/comment','/src/views/vms/comment/index.vue','i-fe:message-square',5,1,1,0,NULL,NULL,NOW(),NOW()),
(41,'RoleMgt','角色管理','MENU',4,'/admin/pms/role','/src/views/pms/role/index.vue','i-fe:shield',1,1,1,0,NULL,NULL,NOW(),NOW()),
(42,'PermissionMgt','权限管理','MENU',4,'/admin/pms/resource','/src/views/pms/resource/index.vue','i-fe:lock',2,1,1,0,NULL,NULL,NOW(),NOW()),
(43,'ConfigMgt','配置管理','MENU',4,'/admin/pms/config','/src/views/pms/config/index.vue','i-fe:sliders',3,1,1,0,NULL,NULL,NOW(),NOW()),
(100,'user:add','新增用户','BUTTON',2,NULL,NULL,NULL,1,0,1,0,NULL,NULL,NOW(),NOW()),
(101,'user:edit','编辑用户','BUTTON',2,NULL,NULL,NULL,2,0,1,0,NULL,NULL,NOW(),NOW()),
(102,'user:delete','删除用户','BUTTON',2,NULL,NULL,NULL,3,0,1,0,NULL,NULL,NOW(),NOW()),
(103,'video:add','新增视频','BUTTON',31,NULL,NULL,NULL,1,0,1,0,NULL,NULL,NOW(),NOW()),
(104,'video:edit','编辑视频','BUTTON',31,NULL,NULL,NULL,2,0,1,0,NULL,NULL,NOW(),NOW()),
(105,'video:delete','删除视频','BUTTON',31,NULL,NULL,NULL,3,0,1,0,NULL,NULL,NOW(),NOW());

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_user_role` VALUES (1,1),(2,2);

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_role_permission` VALUES (1,1),(1,2),(1,3),(1,4),(1,31),(1,32),(1,33),(1,34),(1,35),(1,41),(1,42),(1,43),(1,100),(1,101),(1,102),(1,103),(1,104),(1,105),(3,1),(3,2),(3,3),(3,31),(3,32),(3,33),(3,34),(3,35),(3,100),(3,101),(3,102),(3,103),(3,104),(3,105);

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL,
  `config_value` text,
  `config_type` varchar(20) DEFAULT 'STRING',
  `description` varchar(255) DEFAULT NULL,
  `is_system` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_config` VALUES 
(1,'site_name','VMS动漫视频站','STRING','站点名称',1,NOW(),NOW()),
(2,'site_logo','/logo.png','STRING','站点Logo',1,NOW(),NOW()),
(3,'upload_max_size','104857600','NUMBER','最大上传大小',1,NOW(),NOW()),
(4,'allowed_video_types','mp4,webm,mkv,avi,mov','STRING','允许的视频格式',1,NOW(),NOW()),
(5,'allowed_image_types','jpg,jpeg,png,gif,webp','STRING','允许的图片格式',1,NOW(),NOW()),
(6,'video_default_status','0','NUMBER','视频默认状态',1,NOW(),NOW()),
(7,'comment_need_audit','false','BOOLEAN','评论是否需要审核',0,NOW(),NOW()),
(8,'register_enabled','true','BOOLEAN','是否开放注册',0,NOW(),NOW()),
(9,'default_cover','/media/images/default/cover.png','STRING','默认封面图片',1,NOW(),NOW());

-- ========================================
-- 二、视频模块
-- ========================================

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `slug` varchar(50) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `parent_id` bigint DEFAULT '0',
  `sort` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `category` VALUES 
(1,'电影','movie','https://img.icons8.com/color/96/movie.png','院线大片、经典电影',0,1,1,0,NOW()),
(2,'电视剧','tv','https://img.icons8.com/color/96/tv-show.png','热播剧集、网剧',0,2,1,0,NOW()),
(3,'动漫','anime','https://img.icons8.com/color/96/anime.png','日本动漫、国产动画',0,3,1,0,NOW()),
(4,'综艺','variety',NULL,'综艺节目',0,4,1,0,NOW()),
(5,'纪录片','documentary',NULL,'纪录片',0,5,1,0,NOW()),
(6,'游戏','game',NULL,'游戏视频',0,6,1,0,NOW()),
(7,'音乐','music',NULL,'音乐MV',0,7,1,0,NOW()),
(8,'短视频','short',NULL,'短视频',0,8,1,0,NOW());

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `color` varchar(20) DEFAULT '#409EFF',
  `use_count` int DEFAULT '0',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `tag` VALUES 
(1,'热门','#F56C6C',200,0,NOW()),
(2,'推荐','#E6A23C',180,0,NOW()),
(3,'新上线','#67C23A',160,0,NOW()),
(4,'经典','#409EFF',150,0,NOW()),
(5,'高分','#FB7299',140,0,NOW()),
(6,'热血','#FF4757',250,0,NOW()),
(7,'治愈','#70A1FF',220,0,NOW()),
(8,'恋爱','#FF6B81',200,0,NOW()),
(9,'校园','#7BED9F',190,0,NOW()),
(10,'奇幻','#9B59B6',185,0,NOW()),
(11,'冒险','#F39C12',180,0,NOW()),
(12,'搞笑','#FFC312',175,0,NOW()),
(13,'战斗','#E74C3C',170,0,NOW()),
(14,'悬疑','#2C3E50',155,0,NOW()),
(15,'运动','#2ECC71',150,0,NOW()),
(16,'番剧','#00D2D3',500,0,NOW()),
(17,'剧场版','#9B59B6',120,0,NOW()),
(18,'科幻','#3498DB',110,0,NOW()),
(19,'异世界','#E056FD',100,0,NOW()),
(20,'后宫','#FF6B81',90,0,NOW());

DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `intro` text,
  `cover_url` varchar(500) DEFAULT '/media/images/default/cover.png',
  `video_url` varchar(500) DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `status` tinyint DEFAULT '0',
  `view_count` int unsigned DEFAULT '0',
  `like_count` int unsigned DEFAULT '0',
  `favorite_count` int unsigned DEFAULT '0',
  `comment_count` int unsigned DEFAULT '0',
  `duration` int DEFAULT '0',
  `is_vip` tinyint DEFAULT '0',
  `publish_time` datetime DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_view` (`view_count` DESC),
  KEY `idx_create` (`create_time` DESC),
  CONSTRAINT `fk_video_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_video_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动漫视频数据 (使用可播放的MP4视频URL)
-- 视频来源: 公开测试视频 / 开源电影片段
INSERT INTO `video` VALUES 
(1,'鬼灭之刃：无限列车篇','炭治郎与善逸、伊之助一同踏上无限列车,与炎柱一起对抗上弦之叁','https://cdn.myanimelist.net/images/anime/1286/99889l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,125000,8500,3200,450,596,0,NOW(),0,NOW(),NOW()),
(2,'进击的巨人 最终季','一百年前人类被巨人消灭,艾伦发誓要消灭所有巨人','https://cdn.myanimelist.net/images/anime/1000/110531l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,180000,12000,5600,890,596,0,NOW(),0,NOW(),NOW()),
(3,'咒术回战 第二季','虎杖悠仁成为宿傩的容器,涩谷事变篇震撼来袭','https://cdn.myanimelist.net/images/anime/1171/109222l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,95000,6800,2100,320,596,0,NOW(),0,NOW(),NOW()),
(4,'间谍过家家 SPY×FAMILY','代号黄昏的间谍组建临时家庭,温馨又刺激','https://cdn.myanimelist.net/images/anime/1441/122795l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,85000,7200,2800,560,596,0,NOW(),0,NOW(),NOW()),
(5,'蓝色监狱 BLUE LOCK','日本足球协会打造世界第一的利己主义前锋','https://cdn.myanimelist.net/images/anime/1258/126929l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,72000,4500,1800,280,596,0,NOW(),0,NOW(),NOW()),
(6,'葬送的芙莉莲','精灵魔法使芙莉莲开始思考人类的短暂与相遇的意义','https://cdn.myanimelist.net/images/anime/1015/138006l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,110000,9500,4200,680,596,0,NOW(),0,NOW(),NOW()),
(7,'我推的孩子','产科医生转生成偶像的孩子,开始复仇之路','https://cdn.myanimelist.net/images/anime/1812/134736l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,142000,11000,4800,920,596,0,NOW(),0,NOW(),NOW()),
(8,'药屋少女的呢喃','猫猫凭借药学知识解开后宫谜团','https://cdn.myanimelist.net/images/anime/1708/138033l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,88000,6200,2600,340,596,0,NOW(),0,NOW(),NOW()),
(9,'迷宫饭','为救被红龙吞噬的妹妹用魔物料理补充体力','https://cdn.myanimelist.net/images/anime/1711/142478l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,65000,4800,1900,260,596,0,NOW(),0,NOW(),NOW()),
(10,'你的名字','东京高中生与地方小镇女高中生在梦中交换身体','https://cdn.myanimelist.net/images/anime/5/87048l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,220000,18000,9500,1200,596,1,NOW(),0,NOW(),NOW()),
(11,'铃芽之旅','17岁少女铃芽踏上穿越日本的旅程','https://cdn.myanimelist.net/images/anime/1154/139601l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,135000,10500,4600,580,596,0,NOW(),0,NOW(),NOW()),
(12,'天气之子','高中生帆高遇到能操控天气的少女阳菜','https://cdn.myanimelist.net/images/anime/1880/101146l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,118000,8800,3800,420,596,0,NOW(),0,NOW(),NOW()),
(13,'一拳超人','秃头英雄埼玉任何敌人只需一拳解决','https://cdn.myanimelist.net/images/anime/12/76049l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,165000,12500,5200,780,596,0,NOW(),0,NOW(),NOW()),
(14,'钢之炼金术师FA','爱德华兄弟踏上寻找贤者之石之旅','https://cdn.myanimelist.net/images/anime/1223/96541l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,195000,15000,7200,980,596,0,NOW(),0,NOW(),NOW()),
(15,'死亡笔记','高中生夜神月捡到死神笔记开始制裁罪犯','https://cdn.myanimelist.net/images/anime/9/9453l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,175000,13500,6100,850,596,0,NOW(),0,NOW(),NOW()),
(16,'辉夜大小姐想让我告白','学生会会长与副会长的恋爱头脑战','https://cdn.myanimelist.net/images/anime/1295/106551l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,92000,7600,3100,520,596,0,NOW(),0,NOW(),NOW()),
(17,'紫罗兰永恒花园','战争武器少女薇尔莉特成为代笔作家','https://cdn.myanimelist.net/images/anime/1795/95088l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,108000,9200,4500,680,596,1,NOW(),0,NOW(),NOW()),
(18,'四月是你的谎言','钢琴天才与小提琴少女的感人故事','https://cdn.myanimelist.net/images/anime/3/67177l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,135000,11000,5200,750,596,0,NOW(),0,NOW(),NOW()),
(19,'夏目友人帐','能看见妖怪的少年夏目治愈人心的故事','https://cdn.myanimelist.net/images/anime/1064/142774l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,125000,10500,4800,620,596,0,NOW(),0,NOW(),NOW()),
(20,'Re从零开始的异世界生活','菜月昴被召唤到异世界拥有死亡回归能力','https://cdn.myanimelist.net/images/anime/1522/128039l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,155000,12800,5800,820,596,0,NOW(),0,NOW(),NOW()),
(21,'无职转生','34岁废宅转生异世界认真活下去','https://cdn.myanimelist.net/images/anime/1530/117776l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,138000,11200,4900,720,596,0,NOW(),0,NOW(),NOW()),
(22,'千与千寻','少女千寻误入神灵世界的奥斯卡获奖作品','https://cdn.myanimelist.net/images/anime/6/79597l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,250000,22000,12000,1500,596,1,NOW(),0,NOW(),NOW()),
(23,'哈尔的移动城堡','少女苏菲被诅咒变老遇到魔法师哈尔','https://cdn.myanimelist.net/images/anime/5/75810l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,185000,16500,8200,1100,596,0,NOW(),0,NOW(),NOW()),
(24,'龙猫','两姐妹在乡下遇到森林精灵龙猫','https://cdn.myanimelist.net/images/anime/4/75923l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,175000,15500,7800,980,596,0,NOW(),0,NOW(),NOW()),
(25,'电锯人','电次与恶魔波奇塔融合成为电锯人','https://cdn.myanimelist.net/images/anime/1806/126216l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,148000,12200,5400,780,596,0,NOW(),0,NOW(),NOW()),
(26,'孤独摇滚','社恐少女后藤一里加入乐队追逐音乐梦想','https://cdn.myanimelist.net/images/anime/1448/127956l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,105000,8900,4100,620,596,0,NOW(),0,NOW(),NOW()),
(27,'排球少年','乌野高中排球部的热血故事','https://cdn.myanimelist.net/images/anime/7/76014l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,142000,11500,5100,750,596,0,NOW(),0,NOW(),NOW()),
(28,'灌篮高手电影版','湘北VS山王传说中的比赛','https://cdn.myanimelist.net/images/anime/1274/128039l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,188000,16000,7500,1050,596,1,NOW(),0,NOW(),NOW()),
(29,'刀剑神域','桐人被困在虚拟现实游戏中','https://cdn.myanimelist.net/images/anime/11/39717l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',3,1,2,168000,13200,5900,850,596,0,NOW(),0,NOW(),NOW()),
(30,'魔法少女小圆','颠覆魔法少女题材的黑暗神作','https://cdn.myanimelist.net/images/anime/1762/106377l.jpg','https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_2mb.mp4',3,1,2,125000,10500,4800,720,596,0,NOW(),0,NOW(),NOW());

DROP TABLE IF EXISTS `video_episode`;
CREATE TABLE `video_episode` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `file_url` varchar(500) NOT NULL,
  `cover_url` varchar(500) DEFAULT '/media/images/default/cover.png',
  `duration` int DEFAULT '0',
  `sort` int DEFAULT '0',
  `is_free` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_video` (`video_id`),
  CONSTRAINT `fk_episode_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `video_tag_rel`;
CREATE TABLE `video_tag_rel` (
  `video_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`video_id`,`tag_id`),
  KEY `idx_tag` (`tag_id`),
  CONSTRAINT `fk_rel_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rel_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `video_tag_rel` VALUES 
(1,1),(1,6),(1,13),(1,16),(2,1),(2,6),(2,13),(2,14),(2,16),(3,1),(3,6),(3,13),(3,16),(4,1),(4,7),(4,12),(4,16),
(5,6),(5,15),(5,16),(6,1),(6,7),(6,10),(6,11),(6,16),(7,1),(7,14),(7,16),(8,7),(8,14),(8,16),(9,11),(9,12),(9,16),
(10,1),(10,5),(10,8),(10,17),(11,10),(11,11),(11,17),(12,8),(12,10),(12,17),
(13,1),(13,6),(13,12),(13,13),(13,16),(14,1),(14,4),(14,6),(14,11),(14,16),(15,1),(15,14),(15,16),
(16,1),(16,8),(16,9),(16,12),(16,16),(17,1),(17,5),(17,7),(17,16),(18,5),(18,7),(18,8),(18,16),
(19,1),(19,7),(19,16),(20,1),(20,10),(20,19),(20,16),(21,1),(21,10),(21,19),(21,16),
(22,1),(22,4),(22,5),(22,10),(22,17),(23,4),(23,5),(23,10),(23,17),(24,4),(24,5),(24,7),(24,17),
(25,1),(25,6),(25,13),(25,16),(26,1),(26,7),(26,9),(26,16),(27,1),(27,6),(27,15),(27,16),(28,1),(28,4),(28,15),(28,17),
(29,1),(29,10),(29,19),(29,16),(30,1),(30,5),(30,10),(30,16);

-- ========================================
-- 三、互动模块
-- ========================================

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT '0',
  `reply_user_id` bigint DEFAULT NULL,
  `content` text NOT NULL,
  `like_count` int unsigned DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_video` (`video_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `comment` VALUES 
(1,1,2,0,NULL,'炎柱太帅了！这场战斗看哭了😭',256,1,0,NOW()),
(2,1,1,0,NULL,'无限列车篇真的是神作！',189,1,0,NOW()),
(3,2,2,0,NULL,'艾伦到底是怎么想的...这剧情太虐了',320,1,0,NOW()),
(4,6,2,0,NULL,'芙莉莲太治愈了！',167,1,0,NOW()),
(5,4,2,0,NULL,'阿尼亚太可爱了哇库哇库！',456,1,0,NOW()),
(6,10,2,0,NULL,'新海诚永远的神！画面美到窒息',567,1,0,NOW()),
(7,22,2,0,NULL,'千与千寻永远的经典！',678,1,0,NOW()),
(8,14,2,0,NULL,'钢炼神作无疑！',445,1,0,NOW()),
(9,25,2,0,NULL,'电锯人太燃了！',356,1,0,NOW()),
(10,28,1,0,NULL,'灌篮高手YYDS！',567,1,0,NOW());

DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `video_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`,`video_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_fav_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `user_favorite` VALUES (1,1,1,NOW()),(2,1,6,NOW()),(3,1,10,NOW()),(4,1,22,NOW()),(5,2,1,NOW()),(6,2,4,NOW()),(7,2,25,NOW());

DROP TABLE IF EXISTS `user_like`;
CREATE TABLE `user_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `target_type` tinyint NOT NULL,
  `target_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`),
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `user_like` VALUES (1,1,1,1,NOW()),(2,1,1,6,NOW()),(3,1,1,10,NOW()),(4,2,1,1,NOW()),(5,2,1,4,NOW()),(6,2,1,25,NOW());

DROP TABLE IF EXISTS `watch_history`;
CREATE TABLE `watch_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `video_id` bigint NOT NULL,
  `episode_id` bigint DEFAULT NULL,
  `watch_duration` int DEFAULT '0',
  `watch_progress` int DEFAULT '0',
  `last_watch_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`,`video_id`),
  CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_history_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `watch_history` VALUES (1,1,1,NULL,300,300,NOW()),(2,1,6,NULL,200,200,NOW()),(3,2,4,NULL,150,150,NOW()),(4,2,25,NULL,400,400,NOW());

DROP TABLE IF EXISTS `danmaku`;
CREATE TABLE `danmaku` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` varchar(200) NOT NULL,
  `time` float NOT NULL,
  `color` varchar(10) DEFAULT '#FFFFFF',
  `mode` tinyint DEFAULT '1',
  `font_size` tinyint DEFAULT '25',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_video_time` (`video_id`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `danmaku` VALUES 
(1,1,1,'炎柱出场！',10.5,'#FF4757',1,25,NOW()),
(2,1,2,'燃起来了🔥',20.2,'#FFA502',1,25,NOW()),
(3,6,1,'芙莉莲好可爱',15.0,'#70A1FF',1,25,NOW()),
(4,4,1,'哇库哇库！',8.0,'#FF6B81',1,25,NOW()),
(5,10,1,'画面太美了！',5.0,'#70A1FF',1,25,NOW()),
(6,22,2,'千寻加油！',30.0,'#70A1FF',1,25,NOW()),
(7,25,1,'电锯人！！',12.0,'#FF4757',1,25,NOW()),
(8,28,2,'灌篮高手！',18.0,'#E74C3C',1,25,NOW());

-- ========================================
-- 四、轮播图
-- ========================================

DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `img_url` varchar(500) NOT NULL DEFAULT '/media/images/default/cover.png',
  `link_url` varchar(500) DEFAULT NULL,
  `link_type` tinyint DEFAULT '0',
  `target_id` bigint DEFAULT NULL,
  `sort` int DEFAULT '0',
  `is_show` tinyint DEFAULT '1',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `banner` VALUES 
(1,'🔥 鬼灭之刃','https://cdn.myanimelist.net/images/anime/1286/99889l.jpg','/video/1',1,1,1,1,NULL,NULL,0,NOW()),
(2,'⚔️ 进击的巨人','https://cdn.myanimelist.net/images/anime/1000/110531l.jpg','/video/2',1,2,2,1,NULL,NULL,0,NOW()),
(3,'✨ 葬送的芙莉莲','https://cdn.myanimelist.net/images/anime/1015/138006l.jpg','/video/6',1,6,3,1,NULL,NULL,0,NOW()),
(4,'💕 间谍过家家','https://cdn.myanimelist.net/images/anime/1441/122795l.jpg','/video/4',1,4,4,1,NULL,NULL,0,NOW()),
(5,'🌟 你的名字','https://cdn.myanimelist.net/images/anime/5/87048l.jpg','/video/10',1,10,5,1,NULL,NULL,0,NOW()),
(6,'⭐ 千与千寻','https://cdn.myanimelist.net/images/anime/6/79597l.jpg','/video/22',1,22,6,1,NULL,NULL,0,NOW());

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
