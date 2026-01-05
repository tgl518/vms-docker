# Nacos 共享配置使用说明

## 配置步骤

### 1. 在 Nacos 控制台创建共享配置

访问 Nacos 控制台：http://localhost:8848/nacos

登录后，进入 **配置管理** → **配置列表** → **创建配置**

| 字段      | 值                         |
|---------|---------------------------|
| Data ID | `vms-common.yaml`         |
| Group   | `DEFAULT_GROUP`           |
| 配置格式    | YAML                      |
| 配置内容    | 复制 `vms-common.yaml` 文件内容 |

### 2. 各服务模块配置说明

每个服务模块的 `application.yml` 只需保留：

- `server.port` - 服务端口（各服务不同）
- `spring.application.name` - 服务名称
- `spring.cloud.nacos` - Nacos 连接配置 + **共享配置引用**
- `mybatis-plus.type-aliases-package` - 各模块的实体类包（各服务不同）

### 3. 引用共享配置

在各模块的 `application.yml` 中添加：

```yaml
spring:
  cloud:
    nacos:
      config:
        # 引用共享配置
        shared-configs:
          - data-id: vms-common.yaml
            group: DEFAULT_GROUP
            refresh: true
```

## 优点

1. **减少冗余**：公共配置只维护一份
2. **动态刷新**：修改 Nacos 配置后，服务自动刷新，无需重启
3. **集中管理**：数据库密码等敏感信息集中管理
