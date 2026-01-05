# VMS Docker 部署指南

## 📦 项目结构

```
vms-docker-start/
├── docker-compose.yml      # 主编排文件
├── .env.example             # 环境变量模板（复制为 .env）
├── mysql/                   # MySQL 配置和初始化脚本
├── nginx/                   # Nginx 配置
├── backend/                 # 后端源码
├── frontend/                # 前端源码
├── data/                    # 容器数据卷（自动创建）
├── logs/                    # 应用日志输出
└── media/                   # 上传文件存储
```

## 🚀 快速启动

### 1. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，填入实际的密码和密钥
nano .env
```

### 2. 启动所有服务

```bash
# 构建并启动所有服务（首次需要 15-30 分钟）
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 3. 访问服务

| 服务 | 访问地址 |
|------|---------|
| 前端页面 | http://localhost |
| Nacos 控制台 | http://localhost:8848/nacos |
| API 网关 | http://localhost:8080 |

## 🔧 常用命令

```bash
# 停止所有服务
docker-compose down

# 重启某个服务
docker-compose restart vms-gateway

# 查看某个服务日志
docker-compose logs -f vms-user

# 重新构建并启动
docker-compose up -d --build vms-gateway

# 清理所有（包括数据）
docker-compose down -v
```

## 📁 数据卷映射

| 容器路径 | 宿主机路径 | 说明 |
|---------|-----------|------|
| MySQL 数据 | `./data/mysql` | 数据库文件 |
| Nacos 数据 | `./data/nacos` | 配置中心数据 |
| Redis 数据 | `./data/redis` | 缓存数据 |
| 应用日志 | `./logs/` | 各服务日志 |
| 上传文件 | `./media/` | 视频、图片等 |

## ⚠️ 注意事项

1. **首次构建耗时**：后端 6 个 Java 服务构建需要 15-30 分钟
2. **内存需求**：建议至少 8GB 内存
3. **端口占用**：确保 80、3306、6379、8080-8085、8848 端口未被占用

## 🧹 故障排查

### 服务启动失败
```bash
docker-compose logs vms-gateway
```

### 数据库连接失败
```bash
docker exec -it vms-mysql mysql -uroot -p -e "show databases;"
```

### Nacos 注册失败
```bash
curl http://localhost:8848/nacos/
```

