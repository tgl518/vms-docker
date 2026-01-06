-- ========================================
-- VMS-Cloud 数据库初始化脚本 (动漫版 v2)
-- 包含: 表结构 + 丰富动漫演示数据
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

-- 1. 用户表
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

-- 2. 用户档案表
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
(1,'系统管理员','https://cdn.myanimelist.net/images/anime/1015/138006l.jpg',1,'VMS动漫站管理员',NULL,NULL,NOW(),NOW()),
(2,'动漫迷','https://cdn.myanimelist.net/images/anime/1441/122795l.jpg',1,'二次元爱好者~',NULL,NULL,NOW(),NOW());

-- 3. 角色表
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

-- 4. 权限表
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

-- 5. 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_user_role` VALUES (1,1),(2,2);

-- 6. 角色权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sys_role_permission` VALUES (1,1),(1,2),(1,3),(1,4),(1,31),(1,32),(1,33),(1,34),(1,35),(1,41),(1,42),(1,43),(1,100),(1,101),(1,102),(1,103),(1,104),(1,105),(3,1),(3,2),(3,3),(3,31),(3,32),(3,33),(3,34),(3,35),(3,100),(3,101),(3,102),(3,103),(3,104),(3,105);

-- 7. 系统配置表
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
(8,'register_enabled','true','BOOLEAN','是否开放注册',0,NOW(),NOW());

-- ========================================
-- 二、视频模块
-- ========================================

-- 8. 分类表
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

-- 9. 标签表
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

-- 10. 视频表
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `intro` text,
  `cover_url` varchar(500) DEFAULT NULL,
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
  KEY `idx_deleted` (`deleted`),
  FULLTEXT KEY `ft_title` (`title`),
  CONSTRAINT `fk_video_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_video_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动漫视频数据 (使用网络URL)
INSERT INTO `video` VALUES 
-- 热门番剧
(1,'鬼灭之刃：无限列车篇','炭治郎与善逸、伊之助一同踏上无限列车,与炎柱一起对抗上弦之叁。这是一场生死存亡的战斗!','https://cdn.myanimelist.net/images/anime/1286/99889l.jpg','https://www.bilibili.com/video/BV1cs411c7DQ',3,1,2,125000,8500,3200,450,7200,0,NOW(),0,NOW(),NOW()),
(2,'进击的巨人 最终季','一百年前人类被巨人消灭。艾伦在目睹母亲被巨人吞噬后,发誓要消灭所有巨人。','https://cdn.myanimelist.net/images/anime/1000/110531l.jpg','https://www.bilibili.com/video/BV1qK4y1Y7Wm',3,1,2,180000,12000,5600,890,1440,0,NOW(),0,NOW(),NOW()),
(3,'咒术回战 第二季','虎杖悠仁吞下特级咒物成为宿傩的容器,涩谷事变篇震撼来袭！','https://cdn.myanimelist.net/images/anime/1171/109222l.jpg','https://www.bilibili.com/video/BV1dK411f7Es',3,1,2,95000,6800,2100,320,1440,0,NOW(),0,NOW(),NOW()),
(4,'间谍过家家 SPY×FAMILY','代号黄昏的间谍组建临时家庭,妻子是杀手,女儿是超能力者!温馨又刺激!','https://cdn.myanimelist.net/images/anime/1441/122795l.jpg','https://www.bilibili.com/video/BV1FZ4y1T7nc',3,1,2,85000,7200,2800,560,1440,0,NOW(),0,NOW(),NOW()),
(5,'蓝色监狱 BLUE LOCK','日本足球协会启动蓝色监狱计划,打造世界第一的利己主义前锋!','https://cdn.myanimelist.net/images/anime/1258/126929l.jpg','https://www.bilibili.com/video/BV1Lg411P7UL',3,1,2,72000,4500,1800,280,1440,0,NOW(),0,NOW(),NOW()),
(6,'葬送的芙莉莲 Frieren','精灵魔法使芙莉莲击败魔王后,开始思考人类的短暂与相遇的意义。治愈又充满哲思。','https://cdn.myanimelist.net/images/anime/1015/138006l.jpg','https://www.bilibili.com/video/BV1Au4y1Z7mS',3,1,2,110000,9500,4200,680,1440,0,NOW(),0,NOW(),NOW()),
(7,'【推しの子】我推的孩子','产科医生转生成偶像的孩子,开始复仇之路...','https://cdn.myanimelist.net/images/anime/1812/134736l.jpg','https://www.bilibili.com/video/BV1Jo4y1X7Kp',3,1,2,142000,11000,4800,920,5400,0,NOW(),0,NOW(),NOW()),
(8,'药屋少女的呢喃','猫猫凭借药学知识,解开后宫谜团,赢得壬氏赏识...','https://cdn.myanimelist.net/images/anime/1708/138033l.jpg','https://www.bilibili.com/video/BV1Ju4y1c7Qd',3,1,2,88000,6200,2600,340,1440,0,NOW(),0,NOW(),NOW()),
(9,'迷宫饭 Delicious in Dungeon','为救被红龙吞噬的妹妹,一行人用魔物料理补充体力!','https://cdn.myanimelist.net/images/anime/1711/142478l.jpg','https://www.bilibili.com/video/BV1fC4y1K7Lq',3,1,2,65000,4800,1900,260,1440,0,NOW(),0,NOW(),NOW()),

-- 新海诚剧场版
(10,'你的名字。','东京高中生与地方小镇女高中生在梦中交换身体,命运开始转动...','https://cdn.myanimelist.net/images/anime/5/87048l.jpg','https://www.bilibili.com/video/BV1Ws411f7GY',3,1,2,220000,18000,9500,1200,6420,1,NOW(),0,NOW(),NOW()),
(11,'铃芽之旅 Suzume','17岁少女铃芽为关闭引发灾难的门,踏上穿越日本的旅程...','https://cdn.myanimelist.net/images/anime/1154/139601l.jpg','https://www.bilibili.com/video/BV1SM411S7hP',3,1,2,135000,10500,4600,580,7320,0,NOW(),0,NOW(),NOW()),
(12,'天气之子','高中生帆高遇到拥有操控天气能力的少女阳菜,两人命运交织...','https://cdn.myanimelist.net/images/anime/1880/101146l.jpg','https://www.bilibili.com/video/BV1tJ411w7n2',3,1,2,118000,8800,3800,420,6780,0,NOW(),0,NOW(),NOW()),
(13,'秒速5厘米','三个关于距离和时间的短篇故事,诉说着青涩的爱情...','https://cdn.myanimelist.net/images/anime/1410/112994l.jpg','https://www.bilibili.com/video/BV1Ks411f7xm',3,1,2,92000,7200,3100,380,3900,0,NOW(),0,NOW(),NOW()),

-- 经典热血
(14,'一拳超人 One Punch Man','秃头英雄埼玉任何敌人只需一拳解决,寻找强敌的旅程!','https://cdn.myanimelist.net/images/anime/12/76049l.jpg','https://www.bilibili.com/video/BV1Ws411f7gu',3,1,2,165000,12500,5200,780,1440,0,NOW(),0,NOW(),NOW()),
(15,'钢之炼金术师 FA','爱德华兄弟为复活母亲付出惨痛代价,踏上寻找贤者之石之旅...','https://cdn.myanimelist.net/images/anime/1223/96541l.jpg','https://www.bilibili.com/video/BV1ks411f7yN',3,1,2,195000,15000,7200,980,1440,0,NOW(),0,NOW(),NOW()),
(16,'死亡笔记 Death Note','高中生夜神月捡到死神笔记,开始制裁罪犯的计划...','https://cdn.myanimelist.net/images/anime/9/9453l.jpg','https://www.bilibili.com/video/BV1Gs411f7q2',3,1,2,175000,13500,6100,850,1440,0,NOW(),0,NOW(),NOW()),
(17,'JOJO的奇妙冒险','乔斯达家族与DIO的宿命对决,替身能力热血战斗!','https://cdn.myanimelist.net/images/anime/3/40409l.jpg','https://www.bilibili.com/video/BV1es41147Xr',3,1,2,145000,11200,4900,720,1440,0,NOW(),0,NOW(),NOW()),

-- 恋爱校园
(18,'辉夜大小姐想让我告白','学生会会长与副会长的恋爱头脑战!互相喜欢却都不愿先告白','https://cdn.myanimelist.net/images/anime/1295/106551l.jpg','https://www.bilibili.com/video/BV1Bb411P7br',3,1,2,92000,7600,3100,520,1440,0,NOW(),0,NOW(),NOW()),
(19,'紫罗兰永恒花园','战争武器少女薇尔莉特,为理解"我爱你"成为代笔作家...','https://cdn.myanimelist.net/images/anime/1795/95088l.jpg','https://www.bilibili.com/video/BV1Ws411f7hC',3,1,2,108000,9200,4500,680,1440,1,NOW(),0,NOW(),NOW()),
(20,'四月是你的谎言','钢琴天才与小提琴少女的感人故事,音乐与青春的交织...','https://cdn.myanimelist.net/images/anime/3/67177l.jpg','https://www.bilibili.com/video/BV1Ks411f71d',3,1,2,135000,11000,5200,750,1440,0,NOW(),0,NOW(),NOW()),
(21,'龙与虎','大河和龙儿的欢喜冤家恋爱喜剧,经典校园番!','https://cdn.myanimelist.net/images/anime/13/22232l.jpg','https://www.bilibili.com/video/BV1Gs411f7Y2',3,1,2,88000,6800,2900,480,1440,0,NOW(),0,NOW(),NOW()),

-- 治愈系
(22,'夏目友人帐','能看见妖怪的少年夏目,继承了祖母的友人帐,治愈人心的故事...','https://cdn.myanimelist.net/images/anime/1064/142774l.jpg','https://www.bilibili.com/video/BV1es41157R9',3,1,2,125000,10500,4800,620,1440,0,NOW(),0,NOW(),NOW()),
(23,'轻音少女 K-ON!','女子高中轻音部的日常,萌系治愈经典!','https://cdn.myanimelist.net/images/anime/10/76120l.jpg','https://www.bilibili.com/video/BV1Ws411f79j',3,1,2,95000,7800,3400,520,1440,0,NOW(),0,NOW(),NOW()),
(24,'少女终末旅行','末世两个少女驾驶半履带车旅行,废土治愈风格','https://cdn.myanimelist.net/images/anime/4/88019l.jpg','https://www.bilibili.com/video/BV1Ks411F7gC',3,1,2,72000,5600,2400,380,1440,0,NOW(),0,NOW(),NOW()),

-- 异世界
(25,'Re:从零开始的异世界生活','菜月�的被召唤到异世界,拥有死亡回归能力...','https://cdn.myanimelist.net/images/anime/1522/128039l.jpg','https://www.bilibili.com/video/BV1ks411f7ed',3,1,2,155000,12800,5800,820,1440,0,NOW(),0,NOW(),NOW()),
(26,'无职转生','34岁废宅转生异世界,这次要认真活下去!','https://cdn.myanimelist.net/images/anime/1530/117776l.jpg','https://www.bilibili.com/video/BV1HZ4y1r7Xv',3,1,2,138000,11200,4900,720,1440,0,NOW(),0,NOW(),NOW()),
(27,'关于我转生变成史莱姆这档事','社畜转生成史莱姆,建立魔物国家的故事!','https://cdn.myanimelist.net/images/anime/1694/93337l.jpg','https://www.bilibili.com/video/BV1rs411Q7f4',3,1,2,128000,10200,4500,680,1440,0,NOW(),0,NOW(),NOW()),
(28,'为美好的世界献上祝福!','废柴勇者和问题女神的搞笑异世界冒险!','https://cdn.myanimelist.net/images/anime/8/77831l.jpg','https://www.bilibili.com/video/BV1Gs411f7Ym',3,1,2,115000,9200,4100,620,1440,0,NOW(),0,NOW(),NOW()),

-- 宫崎骏
(29,'千与千寻','少女千寻误入神灵世界,在汤婆婆的汤屋工作...奥斯卡获奖作品','https://cdn.myanimelist.net/images/anime/6/79597l.jpg','https://www.bilibili.com/video/BV1ks411f7Qj',3,1,2,250000,22000,12000,1500,7500,1,NOW(),0,NOW(),NOW()),
(30,'哈尔的移动城堡','少女苏菲被诅咒变成老太婆,遇到魔法师哈尔...','https://cdn.myanimelist.net/images/anime/5/75810l.jpg','https://www.bilibili.com/video/BV1Ws411f7Yy',3,1,2,185000,16500,8200,1100,7200,0,NOW(),0,NOW(),NOW()),
(31,'龙猫','两姐妹在乡下遇到森林精灵龙猫,温馨治愈的经典之作','https://cdn.myanimelist.net/images/anime/4/75923l.jpg','https://www.bilibili.com/video/BV1ks411f7nP',3,1,2,175000,15500,7800,980,5100,0,NOW(),0,NOW(),NOW()),
(32,'幽灵公主','少年阿席达卡为解除诅咒,卷入人类与自然的战争...','https://cdn.myanimelist.net/images/anime/7/75919l.jpg','https://www.bilibili.com/video/BV1Gs411f7sD',3,1,2,165000,14000,6800,880,7860,0,NOW(),0,NOW(),NOW()),

-- 更多热门
(33,'电锯人 Chainsaw Man','电次与恶魔波奇塔融合,成为电锯人对抗恶魔!','https://cdn.myanimelist.net/images/anime/1806/126216l.jpg','https://www.bilibili.com/video/BV1WP411H7Hy',3,1,2,148000,12200,5400,780,1440,0,NOW(),0,NOW(),NOW()),
(34,'孤独摇滚!','社恐少女后藤一里加入乐队,追逐音乐梦想!','https://cdn.myanimelist.net/images/anime/1448/127956l.jpg','https://www.bilibili.com/video/BV1oG4y1n7Mw',3,1,2,105000,8900,4100,620,1440,0,NOW(),0,NOW(),NOW()),
(35,'命运之夜 UBW','Fate系列经典,卫宫士郎与Saber的圣杯战争!','https://cdn.myanimelist.net/images/anime/1791/95459l.jpg','https://www.bilibili.com/video/BV1Ws411f7MX',3,1,2,132000,10800,4800,720,1440,0,NOW(),0,NOW(),NOW()),
(36,'工作细胞','拟人化细胞们的体内大冒险,寓教于乐!','https://cdn.myanimelist.net/images/anime/1708/93609l.jpg','https://www.bilibili.com/video/BV1rt41177W4',3,1,2,98000,7800,3400,520,1440,0,NOW(),0,NOW(),NOW()),
(37,'排球少年!!','乌野高中排球部的热血故事,运动番巅峰!','https://cdn.myanimelist.net/images/anime/7/76014l.jpg','https://www.bilibili.com/video/BV1es411f7aB',3,1,2,142000,11500,5100,750,1440,0,NOW(),0,NOW(),NOW()),
(38,'灌篮高手 电影版','湘北VS山王!传说中的比赛终于动画化!','https://cdn.myanimelist.net/images/anime/1274/128039l.jpg','https://www.bilibili.com/video/BV1dP411c7y9',3,1,2,188000,16000,7500,1050,7560,1,NOW(),0,NOW(),NOW()),
(39,'刀剑神域','桐人被困在虚拟现实游戏中,必须通关才能逃出...','https://cdn.myanimelist.net/images/anime/11/39717l.jpg','https://www.bilibili.com/video/BV1es411f7M2',3,1,2,168000,13200,5900,850,1440,0,NOW(),0,NOW(),NOW()),
(40,'魔法少女小圆','颠覆魔法少女题材的黑暗神作!','https://cdn.myanimelist.net/images/anime/1762/106377l.jpg','https://www.bilibili.com/video/BV1Gs411f7nC',3,1,2,125000,10500,4800,720,1440,0,NOW(),0,NOW(),NOW());

-- 11. 视频分集表
DROP TABLE IF EXISTS `video_episode`;
CREATE TABLE `video_episode` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `file_url` varchar(500) NOT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  `duration` int DEFAULT '0',
  `sort` int DEFAULT '0',
  `is_free` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_video` (`video_id`),
  CONSTRAINT `fk_episode_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. 视频标签关联表
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
(10,1),(10,5),(10,8),(10,17),(11,10),(11,11),(11,17),(12,8),(12,10),(12,17),(13,4),(13,8),(13,17),
(14,1),(14,6),(14,12),(14,13),(14,16),(15,1),(15,4),(15,6),(15,11),(15,16),(16,1),(16,14),(16,16),(17,1),(17,6),(17,13),(17,16),
(18,1),(18,8),(18,9),(18,12),(18,16),(19,1),(19,5),(19,7),(19,16),(20,5),(20,7),(20,8),(20,16),(21,8),(21,9),(21,12),(21,16),
(22,1),(22,7),(22,16),(23,7),(23,9),(23,16),(24,7),(24,18),(24,16),
(25,1),(25,10),(25,19),(25,16),(26,1),(26,10),(26,19),(26,16),(27,10),(27,12),(27,19),(27,16),(28,10),(28,12),(28,19),(28,16),
(29,1),(29,4),(29,5),(29,10),(29,17),(30,4),(30,5),(30,10),(30,17),(31,4),(31,5),(31,7),(31,17),(32,4),(32,5),(32,10),(32,17),
(33,1),(33,6),(33,13),(33,16),(34,1),(34,7),(34,9),(34,16),(35,1),(35,6),(35,10),(35,16),(36,7),(36,12),(36,16),(37,1),(37,6),(37,15),(37,16),(38,1),(38,4),(38,15),(38,17),(39,1),(39,10),(39,19),(39,16),(40,1),(40,5),(40,10),(40,16);

-- ========================================
-- 三、互动模块
-- ========================================

-- 13. 评论表
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
(2,1,1,0,NULL,'无限列车篇真的是神作！画面质量爆表',189,1,0,NOW()),
(3,2,2,0,NULL,'艾伦到底是怎么想的...这剧情太虐了',320,1,0,NOW()),
(4,2,1,0,NULL,'最终季的战斗场面太震撼了！',278,1,0,NOW()),
(5,6,2,0,NULL,'这部番太治愈了，芙莉莲超可爱！',167,1,0,NOW()),
(6,6,1,0,NULL,'欣梅尔才是真正的勇者啊...',198,1,0,NOW()),
(7,4,2,0,NULL,'阿尼亚太可爱了哇库哇库！',456,1,0,NOW()),
(8,4,1,0,NULL,'这一家人的互动太有爱了',289,1,0,NOW()),
(9,10,2,0,NULL,'新海诚永远的神！画面美到窒息',567,1,0,NOW()),
(10,10,1,0,NULL,'前前前世太好听了，已循环一万遍',378,1,0,NOW()),
(11,7,2,0,NULL,'第一集就封神！开局直接高能',398,1,0,NOW()),
(12,7,1,0,NULL,'YOASOBI的OP简直绝了！',512,1,0,NOW()),
(13,29,2,0,NULL,'宫崎骏永远的神作！千寻真勇敢',678,1,0,NOW()),
(14,29,1,0,NULL,'这部电影陪伴了我的童年',589,1,0,NOW()),
(15,15,2,0,NULL,'钢炼神作无疑！大佐帅炸了',445,1,0,NOW()),
(16,15,1,0,NULL,'等价交换的道理让我记忆深刻',387,1,0,NOW()),
(17,33,2,0,NULL,'电锯人太燃了！MAPPA制作太顶了',356,1,0,NOW()),
(18,34,1,0,NULL,'波奇酱社恐代入感太强了😂',423,1,0,NOW()),
(19,38,2,0,NULL,'湘北VS山王！等了多少年的动画版！',567,1,0,NOW()),
(20,22,1,0,NULL,'夏目太治愈了，每一集都想哭',334,1,0,NOW());

-- 14. 用户收藏表
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

INSERT INTO `user_favorite` VALUES 
(1,1,1,NOW()),(2,1,6,NOW()),(3,1,10,NOW()),(4,1,29,NOW()),(5,1,15,NOW()),
(6,2,1,NOW()),(7,2,4,NOW()),(8,2,7,NOW()),(9,2,29,NOW()),(10,2,33,NOW());

-- 15. 用户点赞表
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

INSERT INTO `user_like` VALUES 
(1,1,1,1,NOW()),(2,1,1,6,NOW()),(3,1,1,10,NOW()),(4,1,1,29,NOW()),(5,1,1,15,NOW()),
(6,2,1,1,NOW()),(7,2,1,4,NOW()),(8,2,1,7,NOW()),(9,2,1,33,NOW()),(10,2,1,38,NOW());

-- 16. 观看历史表
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

INSERT INTO `watch_history` VALUES 
(1,1,1,NULL,3600,3600,NOW()),(2,1,6,NULL,1200,1200,NOW()),(3,1,10,NULL,5400,5400,NOW()),(4,1,29,NULL,7200,7200,NOW()),
(5,2,4,NULL,720,720,NOW()),(6,2,7,NULL,2400,2400,NOW()),(7,2,33,NULL,1440,1440,NOW()),(8,2,38,NULL,4800,4800,NOW());

-- 17. 弹幕表
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
(1,1,1,'炎柱出场！',120.5,'#FF4757',1,25,NOW()),
(2,1,2,'燃起来了🔥',180.2,'#FFA502',1,25,NOW()),
(3,1,1,'这一击帅爆！',240.8,'#FF6B81',1,25,NOW()),
(4,2,1,'艾伦变了...',60.0,'#2C3E50',1,25,NOW()),
(5,2,2,'这场战斗太精彩了',300.2,'#E74C3C',1,25,NOW()),
(6,6,1,'芙莉莲好可爱',30.0,'#70A1FF',1,25,NOW()),
(7,6,2,'这节奏真舒服',120.5,'#7BED9F',1,25,NOW()),
(8,4,1,'哇库哇库！',15.0,'#FF6B81',1,25,NOW()),
(9,4,2,'阿尼亚赛高！',60.5,'#FF6B81',1,25,NOW()),
(10,10,1,'画面太美了！',10.0,'#70A1FF',1,25,NOW()),
(11,10,2,'新海诚yyds',60.0,'#9B59B6',1,25,NOW()),
(12,10,1,'前前前世来了！',120.5,'#FF4757',1,25,NOW()),
(13,29,1,'千寻加油！',180.0,'#70A1FF',1,25,NOW()),
(14,29,2,'无脸男好可怜',240.0,'#2C3E50',1,25,NOW()),
(15,15,1,'等价交换！',60.0,'#FFA502',1,25,NOW()),
(16,15,2,'大佐！！！',180.5,'#E74C3C',1,25,NOW()),
(17,33,1,'电锯人！！',30.0,'#FF4757',1,25,NOW()),
(18,33,2,'波奇塔可爱',90.0,'#FFC312',1,25,NOW()),
(19,38,1,'灌篮高手YYDS',10.0,'#FF4757',1,25,NOW()),
(20,38,2,'流川枫帅！',120.0,'#3498DB',1,25,NOW());

-- ========================================
-- 四、运营模块 - 轮播图
-- ========================================

DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `img_url` varchar(500) NOT NULL,
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

-- 使用网络高清横幅图
INSERT INTO `banner` VALUES 
(1,'🔥 鬼灭之刃 无限列车篇','https://img1.doubanio.com/view/photo/l/public/p2625893421.webp','/video/1',1,1,1,1,NULL,NULL,0,NOW()),
(2,'⚔️ 进击的巨人 最终季','https://img2.doubanio.com/view/photo/l/public/p2629391767.webp','/video/2',1,2,2,1,NULL,NULL,0,NOW()),
(3,'✨ 葬送的芙莉莲','https://img1.doubanio.com/view/photo/l/public/p2899536188.webp','/video/6',1,6,3,1,NULL,NULL,0,NOW()),
(4,'💕 间谍过家家','https://img9.doubanio.com/view/photo/l/public/p2870136454.webp','/video/4',1,4,4,1,NULL,NULL,0,NOW()),
(5,'🌟 你的名字','https://img2.doubanio.com/view/photo/l/public/p2395733377.webp','/video/10',1,10,5,1,NULL,NULL,0,NOW()),
(6,'⭐ 千与千寻','https://img1.doubanio.com/view/photo/l/public/p2557573348.webp','/video/29',1,29,6,1,NULL,NULL,0,NOW()),
(7,'🏀 灌篮高手 电影版','https://img2.doubanio.com/view/photo/l/public/p2885542436.webp','/video/38',1,38,7,1,NULL,NULL,0,NOW()),
(8,'⚡ 电锯人','https://img1.doubanio.com/view/photo/l/public/p2883795838.webp','/video/33',1,33,8,1,NULL,NULL,0,NOW());

-- ========================================
-- 完成
-- ========================================
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
