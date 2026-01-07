# VMS 视频管理系统 - 部署文档

## 一、环境要求

### 1.1 服务器要求
- **操作系统**: Linux (推荐 Ubuntu 20.04+ / CentOS 7+)
- **内存**: 最低 4GB，推荐 8GB+
- **磁盘**: 最低 20GB 可用空间

### 1.2 软件依赖
- **Docker**: 20.10+
- **Docker Compose**: 2.0+

## 二、快速部署

### 2.1 克隆项目
```bash
git clone <仓库地址>
cd vms-docker
```

### 2.2 配置环境变量
```bash
cp .env.example .env
# 编辑 .env 文件，配置数据库密码等敏感信息
```

### 2.3 启动服务
```bash
docker-compose up -d
```

### 2.4 访问系统
- **前端门户**: http://localhost
- **后台管理**: http://localhost/#/admin
- **默认管理员账号**: admin / 123456

## 三、服务架构

| 服务名称 | 端口 | 说明 |
|---------|------|------|
| nginx | 80 | 前端静态资源 + 反向代理 |
| gateway | 8080 | API 网关 |
| vms-user | 8081 | 用户服务 |
| vms-video | 8082 | 视频服务 |
| vms-interaction | 8083 | 互动服务 (评论/弹幕) |
| vms-file | 8084 | 文件服务 |
| vms-operation | 8085 | 运营服务 (轮播图) |
| mysql | 3306 | 数据库 |
| redis | 6379 | 缓存 |
| nacos | 8848 | 服务注册中心 |

## 四、配置说明

### 4.1 环境变量 (.env)
```bash
# MySQL 配置
MYSQL_ROOT_PASSWORD=your_password
MYSQL_DATABASE=vms_cloud

# Redis 配置
REDIS_PASSWORD=your_redis_password

# JWT 密钥
JWT_SECRET=your_jwt_secret
```

### 4.2 Nginx 配置
配置文件位置: `nginx/nginx.conf`

主要配置：
- 前端静态资源代理
- API 请求转发到网关服务

## 五、构建说明

### 5.1 前端构建
```bash
cd frontend/vedio-system
npm install
npm run build
# 构建产物在 dist/ 目录
cp -r dist ../dist
```

### 5.2 后端构建
```bash
cd backend/VMS-springcloud
mvn clean package -DskipTests
# 各服务 JAR 包在对应模块的 target/ 目录
```

### 5.3 Docker 镜像构建
```bash
# 构建前端镜像
cd frontend
docker build -t vms-frontend .

# 构建后端镜像
cd backend
docker build -t vms-backend .
```

## 六、常用命令

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 查看服务日志
docker-compose logs -f <服务名>

# 重启单个服务
docker-compose restart <服务名>

# 重新构建并启动
docker-compose up -d --build
```

## 七、数据备份

### 7.1 数据库备份
```bash
docker exec vms-mysql mysqldump -u root -p vms_cloud > backup.sql
```

### 7.2 媒体文件备份
```bash
tar -czvf media_backup.tar.gz media/
```

## 八、故障排查

### 8.1 服务无法启动
```bash
# 检查容器状态
docker-compose ps

# 查看详细日志
docker-compose logs <服务名>
```

### 8.2 前端页面无法访问
1. 检查 nginx 服务是否运行
2. 检查端口 80 是否被占用
3. 查看 nginx 日志: `docker logs vms-nginx`

### 8.3 API 请求失败
1. 检查网关服务是否运行
2. 检查 Nacos 服务注册是否正常
3. 查看网关日志: `docker logs vms-gateway`

## 九、生产环境建议

1. **HTTPS 配置**: 使用 Nginx 配置 SSL 证书
2. **数据库**: 使用独立的 MySQL 服务器
3. **缓存**: 配置 Redis 持久化
4. **日志**: 配置 ELK 或其他日志收集系统
5. **监控**: 使用 Prometheus + Grafana 监控服务状态
6. **备份**: 定期备份数据库和媒体文件
