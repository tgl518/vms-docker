# VMS用户模块JMeter性能测试指南

## 📋 测试计划概述

本测试计划针对VMS视频管理系统的**用户模块**进行性能测试，包含以下4个测试场景：

| 场景 | 描述 | 并发数 | 持续时间 | 循环次数 |
|------|------|--------|----------|----------|
| 场景1 | 用户登录压力测试 | 100 | 120秒 | 10次 |
| 场景2 | 用户注册压力测试 | 50 | 60秒 | 5次 |
| 场景3 | 用户信息查询压力测试 | 200 | 90秒 | 20次 |
| 场景4 | 混合业务场景测试 | 100 | 180秒 | 5次 |

---

## 🛠️ 环境准备

### 1. 安装JMeter (Windows)

1. 从官网下载JMeter: https://jmeter.apache.org/download_jmeter.cgi
2. 选择 **Binaries** 中的 `apache-jmeter-x.x.x.zip`
3. 解压到任意目录（如 `C:\Tools\apache-jmeter-5.6.3`）
4. 运行 `bin\jmeter.bat` 启动JMeter GUI

> **注意**: JMeter需要Java 8+运行环境，请先安装JDK

### 2. 启动后端服务

确保以下服务已启动：

```bash
# 基础设施
- Nacos (localhost:8848)
- Redis (localhost:6379)
- MySQL (localhost:3306)

# 微服务
- vms-gateway (localhost:8080)
- vms-user
```

### 3. 确认测试账户

测试计划中使用的默认账户：
- 用户名: `admin`
- 密码: `123456`

如需修改，请在JMeter中编辑对应的HTTP请求。

---

## 🚀 运行测试

### 方式一：GUI模式（推荐调试时使用）

1. 双击 `bin\jmeter.bat` 启动JMeter
2. 点击 **文件 → 打开**，选择 `vms-user-performance-test.jmx`
3. 点击绿色的 **启动** 按钮 (或按 `Ctrl+R`)
4. 查看 **聚合报告** 或 **汇总报告** 获取测试结果

### 方式二：命令行模式（推荐正式测试时使用）

```bash
# 进入JMeter目录
cd C:\Tools\apache-jmeter-5.6.3

# 运行测试并生成HTML报告
bin\jmeter -n -t "path\to\vms-user-performance-test.jmx" -l results.jtl -e -o report

# 参数说明：
# -n: 非GUI模式
# -t: 测试计划文件路径
# -l: 结果日志文件
# -e: 测试结束后生成报告
# -o: 报告输出目录
```

---

## 📊 测试场景详解

### 场景1: 用户登录压力测试

**目的**: 测试登录接口在高并发下的性能表现

**配置**:
- 并发用户数: 100
- Ramp-Up时间: 30秒（每秒增加约3.3个用户）
- 循环次数: 10次
- 持续时间: 120秒

**测试接口**:
```
POST /user/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

**包含**:
- JSON提取器：提取返回的Token
- 响应断言：验证HTTP状态码为200

---

### 场景2: 用户注册压力测试

**目的**: 测试注册接口的写入性能

**配置**:
- 并发用户数: 50
- Ramp-Up时间: 20秒
- 循环次数: 5次
- 持续时间: 60秒

**测试接口**:
```
POST /user/register
Content-Type: application/json

{
  "username": "testuser_${userNum}_${randomSuffix}",
  "password": "Test@123456",
  "email": "test_${userNum}_${randomSuffix}@example.com",
  "nickname": "测试用户${userNum}"
}
```

**特点**:
- 使用计数器生成唯一用户编号
- 使用随机变量避免用户名重复

---

### 场景3: 用户信息查询压力测试

**目的**: 测试查询接口的读取性能

**配置**:
- 并发用户数: 200
- Ramp-Up时间: 30秒
- 循环次数: 20次
- 持续时间: 90秒

**测试接口**:
```
GET /user/info/${userId}    # 根据ID查询用户
GET /user/count             # 获取用户总数
```

---

### 场景4: 混合业务场景测试

**目的**: 模拟真实用户操作流程

**配置**:
- 并发用户数: 100
- Ramp-Up时间: 30秒
- 循环次数: 5次
- 持续时间: 180秒

**测试流程**:
1. 用户登录 → 提取Token
2. 思考时间(500ms)
3. 获取当前用户信息（携带Token）
4. 思考时间(300ms)
5. 获取用户权限（携带Token）

---

## 📈 查看测试结果

### 监听器说明

| 监听器 | 用途 |
|--------|------|
| 查看结果树 | 查看每个请求的详细信息（请求/响应内容） |
| 聚合报告 | 统计每个采样器的性能指标 |
| 汇总报告 | 简洁的汇总统计 |
| 响应时间图 | 响应时间随时间变化的图表 |
| 图形结果 | 多维度性能图表 |

### 关键指标

| 指标 | 说明 | 参考标准 |
|------|------|----------|
| Average | 平均响应时间 | < 200ms 优秀 |
| 90% Line | 90%请求的响应时间 | < 500ms 良好 |
| 99% Line | 99%请求的响应时间 | < 1000ms 可接受 |
| Throughput | 每秒处理请求数 | 越高越好 |
| Error % | 错误率 | < 1% 优秀 |

---

## ⚙️ 自定义配置

### 修改测试服务器地址

1. 打开测试计划
2. 找到 **用户定义的变量**
3. 修改以下变量：
   - `HOST`: 服务器地址（默认 localhost）
   - `PORT`: 网关端口（默认 8080）
   - `PROTOCOL`: 协议（http/https）

### 调整并发参数

在各场景的 **线程组** 中可修改：
- **线程数**: 并发用户数
- **Ramp-Up**: 启动所有线程的时间
- **循环次数**: 每个线程执行的次数
- **持续时间**: 测试持续时间（秒）

### 修改测试账户

在登录请求的 **请求体** 中修改用户名和密码。

---

## 🔧 常见问题

### 1. 连接被拒绝 (Connection refused)

**原因**: 服务未启动或端口不正确

**解决**:
- 确认网关服务(vms-gateway)已启动
- 检查端口是否为8080
- 检查防火墙设置

### 2. 响应超时

**原因**: 服务响应过慢或Nacos服务发现问题

**解决**:
- 增加HTTP请求的超时时间
- 检查Nacos和各微服务状态
- 检查数据库连接

### 3. 登录失败 (401 Unauthorized)

**原因**: 用户名密码错误

**解决**:
- 确认admin账户存在
- 检查密码是否正确
- 查看数据库vms_user表

### 4. OutOfMemoryError

**原因**: JMeter内存不足

**解决**:
编辑 `bin/jmeter.bat`，增加JVM内存：
```
set HEAP=-Xms1g -Xmx4g
```

---

## 📝 测试报告模板

测试完成后，建议记录以下内容：

```markdown
# 性能测试报告

## 测试环境
- 测试日期: 
- JMeter版本: 
- 服务器配置: 
- 网络环境: 

## 测试结果

### 场景1: 用户登录
| 指标 | 结果 |
|------|------|
| 平均响应时间 |  ms |
| 90%响应时间 |  ms |
| 吞吐量 |  req/s |
| 错误率 |  % |

### 场景2: 用户注册
...

## 结论与建议
```

---

## 📚 参考资源

- [JMeter官方文档](https://jmeter.apache.org/usermanual/index.html)
- [JMeter最佳实践](https://jmeter.apache.org/usermanual/best-practices.html)
