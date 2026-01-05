# VMS-SpringCloud 视频管理系统

基于 Spring Cloud 微服务架构的视频管理平台后端。

## 技术栈

| 组件                   | 版本         | 说明            |
|----------------------|------------|---------------|
| JDK                  | 21         | Java 开发环境     |
| Spring Boot          | 3.3.6      | 基础框架          |
| Spring Cloud         | 2024.0.0   | 微服务组件         |
| Spring Cloud Alibaba | 2023.0.3.2 | Nacos 等阿里巴巴组件 |
| Nacos                | 2.x        | 服务注册发现 + 配置中心 |
| Spring Cloud Gateway | 4.x        | API 网关        |
| OpenFeign            | 4.x        | 服务间调用         |
| MyBatis-Plus         | 3.5.8      | ORM 框架        |
| MySQL                | 8.x        | 关系数据库         |
| Redis                | 7.x        | 缓存数据库         |
| Knife4j              | 4.5.0      | API 文档        |

## 项目结构

```
VMS-springcloud/
├── pom.xml                 # 父 POM (版本管理)
├── vmscloud.sql            # 数据库初始化脚本
├── vms-common/             # 公共模块
├── vms-gateway/            # API 网关 (8080)
├── vms-user/               # 用户服务 (8081)
├── vms-video/              # 视频服务 (8082)
├── vms-interaction/        # 互动服务 (8083)
├── vms-operation/          # 运营服务 (8084)
└── vms-file/               # 文件服务 (8085)
```

## 依赖服务 (Docker)

项目依赖以下 Docker 容器服务：

```bash
# MySQL
docker run -d --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=135781012q \
  mysql:8.0

# Nacos (服务注册发现 + 配置中心)
docker run -d --name nacos \
  -p 8848:8848 \
  -p 9848:9848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.3.0

# Redis
docker run -d --name redis \
  -p 6379:6379 \
  redis:7-alpine

# Nginx (反向代理/静态资源)
docker run -d --name nginx \
  -p 80:80 \
  -v /path/to/html:/usr/share/nginx/html \
  -v /path/to/nginx.conf:/etc/nginx/nginx.conf \
  nginx:alpine
```

### 服务访问地址

| 服务        | 地址                          | 说明                |
|-----------|-----------------------------|-------------------|
| Nacos 控制台 | http://localhost:8848/nacos | 账号: nacos / nacos |
| MySQL     | localhost:3306              | 数据库               |
| Redis     | localhost:6379              | 缓存                |
| Nginx     | http://localhost:80         | 前端/静态资源           |

## 快速开始

### 1. 初始化数据库

```bash
# 登录 MySQL 执行初始化脚本
mysql -uroot -p135781012q < vmscloud.sql
```

### 2. 启动顺序

1. 确保 Docker 容器 (MySQL, Nacos, Redis) 已启动
2. 启动各微服务 (可并行)：
    - vms-gateway
    - vms-user
    - vms-video
    - vms-interaction
    - vms-operation
    - vms-file

### 3. 验证

- Nacos 控制台查看服务注册: http://localhost:8848/nacos
- API 网关入口: http://localhost:8080
- 各服务 API 文档: http://localhost:{port}/doc.html

## 代码生成器

项目提供两种方式快速创建新模块：

### 方式一：Shell 脚本创建模块骨架

```bash
# 创建新模块 (自动生成目录结构、pom.xml、启动类、配置文件)
./create-module.sh vms-video
./create-module.sh vms-interaction
./create-module.sh vms-operation
./create-module.sh vms-file
```

### 方式二：MyBatis-Plus 代码生成器

生成 Entity、Mapper、Service 代码：

```bash
# 1. 先用脚本创建模块骨架
./create-module.sh vms-video

# 2. 编译项目
mvn clean compile -pl vms-common

# 3. 运行代码生成器 (在 vms-common 模块中)
cd vms-common
mvn exec:java -Dexec.mainClass="cn.yznu.vms.generator.VmsCodeGenerator"

# 4. 按提示输入:
#    模块名: vms-video
#    表名: video,video_episode,category,tag,video_tag_rel
```

### 表与模块对应关系

| 模块              | 表名                                                 |
|-----------------|----------------------------------------------------|
| vms-video       | video, video_episode, category, tag, video_tag_rel |
| vms-interaction | comment, user_favorite, user_like, watch_history   |
| vms-operation   | banner                                             |
| vms-file        | 无实体表                                               |

## API 路由

| 路径前缀              | 服务              | 说明          |
|-------------------|-----------------|-------------|
| `/api/user/**`    | vms-user        | 用户认证、信息管理   |
| `/api/video/**`   | vms-video       | 视频、分类、标签、分集 |
| `/api/comment/**` | vms-interaction | 评论管理        |
| `/api/banner/**`  | vms-operation   | 轮播图管理       |
| `/api/file/**`    | vms-file        | 文件上传下载      |
| `/files/**`       | vms-file        | 静态资源访问      |

## 默认账号

| 用户名   | 密码       | 角色  |
|-------|----------|-----|
| admin | admin123 | 管理员 |

## 数据库表

| 表名            | 说明     | 所属服务            |
|---------------|--------|-----------------|
| sys_user      | 用户核心表  | vms-user        |
| sys_user_info | 用户档案表  | vms-user        |
| category      | 视频分类表  | vms-video       |
| tag           | 标签表    | vms-video       |
| video         | 视频主表   | vms-video       |
| video_episode | 视频分集表  | vms-video       |
| video_tag_rel | 视频标签关联 | vms-video       |
| comment       | 评论表    | vms-interaction |
| banner        | 轮播图表   | vms-operation   |
| user_favorite | 用户收藏表  | vms-interaction |
| user_like     | 用户点赞表  | vms-interaction |
| watch_history | 观看历史表  | vms-interaction |
