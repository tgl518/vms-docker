# API 返回格式规范

## 统一响应格式

所有 API 都使用 `Result<T>` 包装，结构如下：

```json
{
  "code": 200,
  "message": "success",
  "data": T
}
```

---

## 列表数据返回规范

### 1. 分页列表（管理后台 CRUD）

**使用场景**：管理后台的表格展示，需要分页

**返回类型**：`Result<PageResult<T>>`

**格式**：

```json
{
  "code": 200,
  "data": {
    "pageData": [...],
    "total": 100,
    "pageNo": 1,
    "pageSize": 10,
    "pages": 10
  }
}
```

**使用方式**：

```java
@GetMapping("/list")
public Result<PageResult<User>> list(...) {
    IPage<User> page = service.page(...);
    return Result.success(PageResult.of(page));
}
```

---

### 2. 非分页列表（管理后台 CRUD，数据量小）

**使用场景**：管理后台表格展示，数据量小无需分页

**返回类型**：`Result<PageResult<T>>`（依然用 PageResult！）

**原因**：前端 MeCrud 组件统一解析 `{ pageData, total }` 格式

**使用方式**：

```java
@GetMapping("/all")
public Result<PageResult<Tag>> all() {
    List<Tag> list = service.list();
    return Result.success(PageResult.of(list, list.size()));
}
```

---

### 3. 简单列表（下拉框、公开 API）

**使用场景**：

- 下拉选择框选项
- 前端用户页面的展示列表
- 子资源列表

**返回类型**：`Result<List<T>>`

**格式**：

```json
{
  "code": 200,
  "data": [...]
}
```

**使用方式**：

```java

@GetMapping("/options")
public Result<List<Category>> options() {
    return Result.success(service.listEnabled());
}
```

---

## 当前接口分类

| 接口路径                   | 用途        | 返回类型           |
|------------------------|-----------|----------------|
| `/user/list`           | 管理后台用户列表  | `PageResult` ✅ |
| `/video/list`          | 管理后台视频列表  | `PageResult` ✅ |
| `/tag/list`            | 管理后台标签列表  | `PageResult` ✅ |
| `/category/all`        | 管理后台分类列表  | `PageResult` ✅ |
| `/banner/all`          | 管理后台轮播图列表 | `PageResult` ✅ |
| `/category/list`       | 下拉框选项     | `List`         |
| `/banner/list`         | 前端轮播图展示   | `List`         |
| `/tag/hot`             | 前端热门标签    | `List`         |
| `/video/{id}/episodes` | 视频分集列表    | `List`         |

---

## 异常处理规范

后端业务逻辑中校验失败或发生错误时，**必须**抛出 `BusinessException`，严禁直接返回 `Result.fail()`。

### 1. 使用预定义错误码

```java
throw new BusinessException(ResultCode.USER_NOT_FOUND);
```

### 2. 使用自定义错误信息

```java
throw new BusinessException(ResultCode.FAIL, "自定义错误信息");
```

### 3. 全局异常处理

所有抛出的 `BusinessException` 会被全局异常处理器捕获，并统一返回如下 JSON 格式：

```json
{
  "code": 500,
  "message": "自定义错误信息",
  "data": null,
  "timestamp": 1701234567890
}
```
