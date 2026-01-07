# 角色管理模块 - 黑盒测试执行指南

## 1. 环境要求

| 项目 | 要求 |
|------|------|
| Java | JDK 17+ |
| Maven | 3.6+ |
| VMS 服务 | 后端服务需运行中 (网关端口 8080) |

## 2. 项目结构

```
测试/黑盒测试/
├── pom.xml                           # Maven 配置
├── vms-user-api.json                 # API 文档 (参考用)
├── 角色管理_等价类记录表.md            # 等价类划分文档
├── 角色管理_测试用例表.md              # 测试用例文档
├── README.md                         # 本文档
└── src/
    └── cn/yznu/vms/user/blackbox/
        ├── ApiTestBase.java          # 测试基础类
        └── RoleManagementBlackboxTest.java  # 测试主类
```

## 3. 运行测试

### 方式一：使用 Maven 运行

```bash
# 1. 进入测试目录
cd 测试/黑盒测试

# 2. 编译项目
mvn compile

# 3. 打包成可执行 JAR
mvn package

# 4. 运行测试
java -jar target/vms-blackbox-test-1.0.0.jar
```

### 方式二：直接编译运行 (无需 Maven)

```bash
# 1. 进入测试目录
cd 测试/黑盒测试

# 2. 下载 Jackson 依赖 (如果没有)
# 你可以从 Maven Central 下载以下 JAR:
# - jackson-databind-2.15.2.jar
# - jackson-core-2.15.2.jar
# - jackson-annotations-2.15.2.jar

# 3. 编译 (假设依赖放在 lib/ 目录)
javac -cp "lib/*" -d out src/cn/yznu/vms/user/blackbox/*.java

# 4. 运行
java -cp "out:lib/*" cn.yznu.vms.user.blackbox.RoleManagementBlackboxTest
```

### 方式三：使用 IDE 运行

1. 用 IntelliJ IDEA 打开 `测试/黑盒测试` 目录作为 Maven 项目
2. 右键点击 `RoleManagementBlackboxTest.java`
3. 选择 "Run 'RoleManagementBlackboxTest.main()'"

## 4. 运行前检查

在运行测试之前，请确保：

1. **后端服务已启动**
   - 网关服务运行在 `http://localhost:8080`
   - vms-user 服务正常运行
   - 数据库和 Redis 正常连接

2. **测试数据存在**
   - 数据库中必须有 `SUPER_ADMIN` 角色 (ID=1)
   - 数据库中必须有 `admin` 角色 (ID=3)
   - 数据库中必须有 `user` 角色 (ID=2)

3. **如在 WSL2 环境**
   - 确保 localhost:8080 可以正常访问
   - 可能需要修改 `ApiTestBase.java` 中的 `BASE_URL` 为宿主机 IP

## 5. 测试输出说明

测试运行时会输出如下格式的结果：

```
【1. 创建角色接口测试 POST /user/role】
─────────────────────────────────────────

TC-C-001: 创建角色-全部有效参数
✅ 成功: 创建成功

TC-C-003: 创建角色-code为空
✅ 预期失败: code=400, message=角色编码不能为空
```

- ✅ 表示测试通过
- ❌ 表示测试失败或异常
- ⚠ 表示测试被跳过

## 6. 常见问题

### Q: 连接被拒绝

```
Connection refused: localhost:8080
```

**解决方案**: 确保后端服务已启动，或修改 `ApiTestBase.java` 中的 `BASE_URL`。

### Q: 403 Forbidden

**原因**: 管理员认证问题  
**解决方案**: 确保 `ApiTestBase.java` 中的 `ADMIN_USER_ID` 对应一个有效的管理员用户。

### Q: 编译错误

**解决方案**: 确保使用 JDK 17+，并且 Jackson 依赖正确配置。

## 7. 测试覆盖范围

| 接口 | 用例数 | 有效等价类 | 无效等价类 |
|------|--------|------------|------------|
| POST /user/role | 15 | 14 | 11 |
| GET /user/role/{id} | 3 | 1 | 3 |
| GET /user/role/page | 10 | 9 | 5 |
| PATCH /user/role/{id} | 10 | 5 | 7 |
| DELETE /user/role/{id} | 5 | 3 | 4 |
| **总计** | **43** | **32** | **30** |
