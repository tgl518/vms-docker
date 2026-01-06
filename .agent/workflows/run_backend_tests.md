---
description: How to run backend unit tests locally without Docker
---

# 如何在本地运行后端单元测试

由于我们编写的是**Mock 驱动的单元测试** (Service 层使用 Mockito, Controller 层使用 @WebMvcTest)，因此**不需要**启动 MySQL、Redis 或 Nacos 等外部服务即可运行。

您只需要确保本地安装了 JDK 17+ 和 Maven。

## 1. 编译公共依赖

首先，需要编译并安装公共模块 (`vms-common`, `vms-api` 等) 到本地 Maven 仓库。

```bash
cd backend/VMS-springcloud
mvn install -DskipTests
```

## 2. 运行 vms-user 测试

```bash
cd vms-user
mvn test
```

## 3. 运行 vms-video 测试

```bash
cd ../vms-video
mvn test
```

## 常见问题

如果遇到连接 Nacos 报错，可以在命令后添加参数禁用 Nacos：
```bash
mvn test -Dspring.cloud.nacos.discovery.enabled=false -Dspring.cloud.nacos.config.enabled=false
```
