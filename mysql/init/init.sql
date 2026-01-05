-- MySQL dump 10.13  Distrib 8.0.44, for Linux (x86_64)
--
-- Host: localhost    Database: vms_cloud
-- ------------------------------------------------------
-- Server version	8.0.44

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

--
-- Current Database: `vms_cloud`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `vms_cloud` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `vms_cloud`;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `img_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转链接',
  `link_type` tinyint DEFAULT '0' COMMENT '类型: 0外链 1视频 2分类',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID',
  `sort` int DEFAULT '0' COMMENT '排序',
  `is_show` tinyint DEFAULT '1' COMMENT '显示: 1是 0否',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `deleted` tinyint DEFAULT '0' COMMENT '软删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sort` (`sort`),
  KEY `idx_show` (`is_show`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'欢迎使用VMS','http://localhost/media/images/2025/12/22/261418343100190720.png','/video/1',1,1,1,1,NULL,NULL,0,'2025-12-22 16:44:57'),(2,'探索精彩视频','https://images.unsplash.com/photo-1485846234645-a62644f84728?w=1200',NULL,0,NULL,2,1,NULL,NULL,0,'2025-12-22 16:44:57');
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `slug` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '别名(英文URL)',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标URL',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `parent_id` bigint DEFAULT '0' COMMENT '父分类ID(0=顶级)',
  `sort` int DEFAULT '0' COMMENT '排序(小优先)',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1启用 0禁用',
  `deleted` tinyint DEFAULT '0' COMMENT '软删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_sort` (`sort`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Movie_Final_Test','movie',NULL,NULL,0,1,1,0,'2025-12-22 16:44:57'),(2,'电影','tv',NULL,NULL,0,2,1,0,'2025-12-22 16:44:57'),(3,'动漫','anime',NULL,NULL,0,3,1,0,'2025-12-22 16:44:57'),(4,'综艺','variety',NULL,NULL,0,4,1,0,'2025-12-22 16:44:57'),(5,'Documentary_Updated','documentary',NULL,NULL,0,5,1,0,'2025-12-22 16:44:57'),(6,'游戏','game',NULL,NULL,0,6,1,0,'2025-12-22 16:44:57'),(7,'音乐','music',NULL,NULL,0,7,1,0,'2025-12-22 16:44:57'),(8,'短视频','short',NULL,NULL,0,8,1,0,'2025-12-22 16:44:57');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论ID(0=顶级)',
  `reply_user_id` bigint DEFAULT NULL COMMENT '回复目标用户ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `like_count` int unsigned DEFAULT '0' COMMENT '点赞数',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1正常 0隐藏',
  `deleted` tinyint DEFAULT '0' COMMENT '软删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_video` (`video_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_create` (`create_time` DESC),
  KEY `idx_deleted` (`deleted`),
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,3,0,NULL,'test001',0,1,0,'2025-12-24 16:32:34'),(2,1,3,0,NULL,'测试评论功能',0,1,0,'2025-12-24 16:37:45'),(3,1,3,0,NULL,'测试评论功能',0,1,0,'2025-12-24 16:46:03'),(4,1,3,0,NULL,'test001',0,1,0,'2025-12-24 16:53:04'),(5,1,3,0,NULL,'🥰',0,1,0,'2025-12-24 17:11:13');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `danmaku`
--

DROP TABLE IF EXISTS `danmaku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danmaku`
--

LOCK TABLES `danmaku` WRITE;
/*!40000 ALTER TABLE `danmaku` DISABLE KEYS */;
INSERT INTO `danmaku` VALUES (1,1,1,'这个视频真不错！',3.5,'#FFFFFF',1,25,'2025-12-24 20:24:31'),(2,1,2,'前排围观',1,'#FB7299',1,25,'2025-12-24 20:24:31'),(3,1,3,'好看好看',8.2,'#00AEEC',1,25,'2025-12-24 20:24:31'),(4,1,4,'你好',6.44843,'#FFFFFF',1,25,'2025-12-24 23:15:27');
/*!40000 ALTER TABLE `danmaku` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键(唯一)',
  `config_value` text COLLATE utf8mb4_unicode_ci COMMENT '配置值',
  `config_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'STRING' COMMENT '类型: STRING/NUMBER/BOOLEAN/JSON',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述',
  `is_system` tinyint DEFAULT '0' COMMENT '系统内置: 1是 0否(系统内置不可删除)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'site_name','VMS视频管理系统','STRING','站点名称',1,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(2,'site_logo','/logo.png','STRING','站点Logo',1,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(3,'upload_max_size','104857600','NUMBER','最大上传大小(字节,默认100MB)',1,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(4,'allowed_video_types','mp4,webm,mkv,avi,mov','STRING','允许的视频格式',1,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(5,'allowed_image_types','jpg,jpeg,png,gif,webp','STRING','允许的图片格式',1,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(6,'video_default_status','0','NUMBER','视频默认状态(0草稿)',1,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(7,'comment_need_audit','false','BOOLEAN','评论是否需要审核',0,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(8,'register_enabled','true','BOOLEAN','是否开放注册',0,'2025-12-22 16:44:57','2025-12-22 16:44:57');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码(唯一)',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'MENU' COMMENT '类型: MENU菜单 BUTTON按钮 API接口',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID(0=顶级)',
  `path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT '0' COMMENT '排序',
  `is_show` tinyint DEFAULT '1' COMMENT '是否显示: 1是 0否',
  `is_enable` tinyint DEFAULT '1' COMMENT '是否启用: 1是 0否',
  `keep_alive` tinyint DEFAULT '0' COMMENT '是否KeepAlive: 1是 0否',
  `layout` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '布局模式',
  `redirect` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '重定向路径',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_type` (`type`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'Dashboard','仪表盘','MENU',0,'/admin/dashboard','/src/views/vms/dashboard/index.vue','i-fe:bar-chart-2',0,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-23 15:09:05'),(2,'UserMgt','用户管理','MENU',0,'/admin/pms/user','/src/views/pms/user/index.vue','i-fe:user',3,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(3,'VideoMgt','视频管理','MENU',0,NULL,NULL,'i-fe:video',2,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(4,'SystemMgt','系统管理','MENU',0,NULL,NULL,'i-fe:settings',10,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(31,'VideoList','视频列表','MENU',3,'/admin/vms/video','/src/views/vms/video/index.vue','i-fe:list',1,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(32,'CategoryList','分类管理','MENU',3,'/admin/vms/category','/src/views/vms/category/index.vue','i-fe:grid',2,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(33,'TagList','标签管理','MENU',3,'/admin/vms/tag','/src/views/vms/tag/index.vue','i-fe:tag',3,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(34,'BannerList','轮播图管理','MENU',3,'/admin/vms/banner','/src/views/vms/banner/index.vue','i-fe:image',4,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(35,'CommentList','评论管理','MENU',3,'/admin/vms/comment','/src/views/vms/comment/index.vue','i-fe:message-square',5,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(41,'RoleMgt','角色管理','MENU',4,'/admin/pms/role','/src/views/pms/role/index.vue','i-fe:shield',1,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-23 12:31:50'),(42,'PermissionMgt','权限管理','MENU',4,'/admin/pms/resource','/src/views/pms/resource/index.vue','i-fe:lock',2,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-23 12:31:50'),(43,'ConfigMgt','配置管理','MENU',4,'/admin/pms/config','/src/views/pms/config/index.vue','i-fe:sliders',3,1,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-23 12:31:50'),(100,'user:add','新增用户','BUTTON',2,NULL,NULL,NULL,1,0,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(101,'user:edit','编辑用户','BUTTON',2,NULL,NULL,NULL,2,0,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(102,'user:delete','删除用户','BUTTON',2,NULL,NULL,NULL,3,0,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(103,'video:add','新增视频','BUTTON',31,NULL,NULL,NULL,1,0,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(104,'video:edit','编辑视频','BUTTON',31,NULL,NULL,NULL,2,0,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(105,'video:delete','删除视频','BUTTON',31,NULL,NULL,NULL,3,0,1,0,NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57');
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码(唯一)',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1启用 0禁用',
  `deleted` tinyint DEFAULT '0' COMMENT '软删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'SUPER_ADMIN','超级管理员','拥有所有权限',0,1,0,'2025-12-24 11:18:51','2025-12-24 11:18:51'),(2,'user','普通用户','普通用户无后台权限',2,1,0,'2025-12-24 11:18:51','2025-12-24 11:18:51'),(3,'admin','管理员','管理员无系统管理权限',1,1,0,'2025-12-24 11:18:51','2025-12-24 11:18:51');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `idx_permission` (`permission_id`),
  CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (1,1),(3,1),(1,2),(3,2),(1,3),(3,3),(1,4),(1,31),(3,31),(1,32),(3,32),(1,33),(3,33),(1,34),(3,34),(1,35),(3,35),(1,41),(1,42),(1,43),(1,100),(3,100),(1,101),(3,101),(1,102),(3,102),(1,103),(3,103),(1,104),(3,104),(1,105),(3,105);
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(BCrypt加密)',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `status` tinyint DEFAULT '1' COMMENT '状态: 1正常 0禁用',
  `deleted` tinyint DEFAULT '0' COMMENT '软删除: 0未删 1已删',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',NULL,NULL,1,0,'2025-12-22 16:44:57','2025-12-23 17:01:14'),(2,'test','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','gsdfgdfs2@gmail.com','130283022',1,0,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(3,'普通管理员','$2a$10$M2K05E6WSUloLFAgpw/O7OW3D3v0QMfBSlS/h1t1Kk/IXDNfWH0De',NULL,NULL,1,0,'2025-12-24 14:41:42','2025-12-24 14:41:42'),(4,'test1','$2a$10$IJR6a4K/xdqjhJ7R1g5JM.zCyR9Nfmgm7xnDUGu.tQxWh2qDUdRnq',NULL,NULL,1,0,'2025-12-24 17:39:13','2025-12-24 17:39:13');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_info`
--

DROP TABLE IF EXISTS `sys_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_info` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '2' COMMENT '性别: 0女 1男 2保密',
  `intro` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所在地',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_userinfo_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_info`
--

LOCK TABLES `sys_user_info` WRITE;
/*!40000 ALTER TABLE `sys_user_info` DISABLE KEYS */;
INSERT INTO `sys_user_info` VALUES (1,'系统管理员','http://localhost/media/images/2025/12/23/261533693284323328.png',1,'',NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(2,'测试用户','https://api.dicebear.com/7.x/avataaars/svg?seed=test',1,'这个人很懒，什么都没写~',NULL,NULL,'2025-12-22 16:44:57','2025-12-22 16:44:57'),(3,'普通管理员1','https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/1144400/ss_c1aa26767e660ca6180d770083ecdc07d38e50d7.1920x1080.jpg?t=1752128014',2,NULL,NULL,NULL,'2025-12-24 14:41:42','2025-12-24 14:41:42'),(4,'深刻理解发撒两节课',NULL,1,'天若有情天易老',NULL,NULL,'2025-12-24 17:39:13','2025-12-24 17:39:13');
/*!40000 ALTER TABLE `sys_user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `idx_role` (`role_id`),
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2),(4,2),(3,3),(4,3);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名',
  `color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '#409EFF' COMMENT '颜色(HEX)',
  `use_count` int DEFAULT '0' COMMENT '使用次数',
  `deleted` tinyint DEFAULT '0' COMMENT '软删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_use_count` (`use_count` DESC),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1,'热门','#F56C6C',100,0,'2025-12-22 16:44:57'),(2,'推荐','#E6A23C',80,0,'2025-12-22 16:44:57'),(3,'新上线','#67C23A',60,0,'2025-12-22 16:44:57'),(4,'经典','#409EFF',50,0,'2025-12-22 16:44:57'),(5,'高分','#FB7299',40,0,'2025-12-22 16:44:57'),(6,'国产','#909399',30,0,'2025-12-22 16:44:57'),(7,'欧美','#2196F3',25,0,'2025-12-22 16:44:57'),(8,'日韩','#9C27B0',20,0,'2025-12-22 16:44:57'),(9,'TestTag111','#409EFF',0,0,'2025-12-25 01:20:22'),(10,'PermissionTest123','#18a058',0,1,'2025-12-25 01:23:27');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_favorite`
--

DROP TABLE IF EXISTS `user_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`,`video_id`),
  KEY `idx_video` (`video_id`),
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_fav_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorite`
--

LOCK TABLES `user_favorite` WRITE;
/*!40000 ALTER TABLE `user_favorite` DISABLE KEYS */;
INSERT INTO `user_favorite` VALUES (6,3,1,'2025-12-24 16:43:23');
/*!40000 ALTER TABLE `user_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_like`
--

DROP TABLE IF EXISTS `user_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` tinyint NOT NULL COMMENT '类型: 1视频 2评论',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`),
  KEY `idx_target` (`target_type`,`target_id`),
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_like`
--

LOCK TABLES `user_like` WRITE;
/*!40000 ALTER TABLE `user_like` DISABLE KEYS */;
INSERT INTO `user_like` VALUES (5,1,1,1,'2025-12-24 11:48:57'),(6,3,1,1,'2025-12-24 16:43:23');
/*!40000 ALTER TABLE `user_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video`
--

DROP TABLE IF EXISTS `video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `intro` text COLLATE utf8mb4_unicode_ci COMMENT '简介',
  `cover_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面URL',
  `video_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频URL(单集)',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `user_id` bigint NOT NULL COMMENT '上传者ID',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0草稿 1审核 2发布 3下架',
  `view_count` int unsigned DEFAULT '0' COMMENT '播放量',
  `like_count` int unsigned DEFAULT '0' COMMENT '点赞数',
  `favorite_count` int unsigned DEFAULT '0' COMMENT '收藏数',
  `comment_count` int unsigned DEFAULT '0' COMMENT '评论数',
  `duration` int DEFAULT '0' COMMENT '时长(秒)',
  `is_vip` tinyint DEFAULT '0' COMMENT 'VIP: 0否 1是',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `deleted` tinyint DEFAULT '0' COMMENT '软删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video`
--

LOCK TABLES `video` WRITE;
/*!40000 ALTER TABLE `video` DISABLE KEYS */;
INSERT INTO `video` VALUES (1,'test','test','http://localhost/media/images/2025/12/22/261418343100190720.png','http://localhost/media/videos/2025/12/22/261418486432141312.mp4',8,1,2,91,0,0,0,100,0,NULL,0,'2025-12-22 17:03:59','2025-12-25 02:12:25');
/*!40000 ALTER TABLE `video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_episode`
--

DROP TABLE IF EXISTS `video_episode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_episode` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分集标题',
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件URL',
  `cover_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面URL',
  `duration` int DEFAULT '0' COMMENT '时长(秒)',
  `sort` int DEFAULT '0' COMMENT '排序',
  `is_free` tinyint DEFAULT '1' COMMENT '免费: 1是 0否',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_video` (`video_id`),
  KEY `idx_sort` (`sort`),
  CONSTRAINT `fk_episode_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频分集表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_episode`
--

LOCK TABLES `video_episode` WRITE;
/*!40000 ALTER TABLE `video_episode` DISABLE KEYS */;
/*!40000 ALTER TABLE `video_episode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_tag_rel`
--

DROP TABLE IF EXISTS `video_tag_rel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_tag_rel` (
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`video_id`,`tag_id`),
  KEY `idx_tag` (`tag_id`),
  CONSTRAINT `fk_rel_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rel_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频标签关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_tag_rel`
--

LOCK TABLES `video_tag_rel` WRITE;
/*!40000 ALTER TABLE `video_tag_rel` DISABLE KEYS */;
INSERT INTO `video_tag_rel` VALUES (1,1),(1,2),(1,3),(1,4),(1,6),(1,9);
/*!40000 ALTER TABLE `video_tag_rel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watch_history`
--

DROP TABLE IF EXISTS `watch_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watch_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `video_id` bigint NOT NULL COMMENT '视频ID',
  `episode_id` bigint DEFAULT NULL COMMENT '分集ID',
  `watch_duration` int DEFAULT '0' COMMENT '观看时长(秒)',
  `watch_progress` int DEFAULT '0' COMMENT '观看进度(秒)',
  `last_watch_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后观看时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`,`video_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_time` (`last_watch_time` DESC),
  KEY `fk_history_video` (`video_id`),
  CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_history_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='观看历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watch_history`
--

LOCK TABLES `watch_history` WRITE;
/*!40000 ALTER TABLE `watch_history` DISABLE KEYS */;
INSERT INTO `watch_history` VALUES (1,1,1,NULL,113,113,'2025-12-24 17:36:06'),(2,3,1,NULL,180,180,'2025-12-24 16:43:52'),(4,4,1,NULL,180,180,'2025-12-24 23:56:00');
/*!40000 ALTER TABLE `watch_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'vms_cloud'
--

--
-- Dumping routines for database 'vms_cloud'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-25  2:21:27
