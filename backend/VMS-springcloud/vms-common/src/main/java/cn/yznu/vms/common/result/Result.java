package cn.yznu.vms.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 统一响应结果封装
 * 所有 API 接口都返回这个格式，方便前端统一处理
 * <p>
 * 使用方式：
 * - 成功无数据: Result.success()
 * - 成功带数据: Result.success(data)
 * - 分页数据:   Result.page(iPage)  或  Result.page(list, total)
 * - 列表数据:   Result.list(list)
 * - 失败:       Result.fail("错误信息")
 *
 * @param <T> 响应数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    // ==================== 私有构造，使用静态方法创建 ====================

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功响应 ====================

    /**
     * 成功 (无数据)
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功 (带数据)
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功 (自定义消息)
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // ==================== 分页响应 (用于管理后台 CRUD 列表) ====================

    /**
     * 分页响应 - 从 MyBatis-Plus IPage 对象创建
     * 返回格式: { pageData: [...], total: n, pageNo: 1, pageSize: 10, pages: 10 }
     *
     * @param page MyBatis-Plus 分页对象
     */
    public static <T> Result<PageResult<T>> page(IPage<T> page) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), PageResult.of(page));
    }

    /**
     * 分页响应 - 从列表创建 (无分页场景，但需要返回分页格式给前端组件)
     * 返回格式: { pageData: [...], total: n }
     *
     * @param list 数据列表
     */
    public static <T> Result<PageResult<T>> page(List<T> list) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(),
                PageResult.of(list, list != null ? list.size() : 0));
    }

    /**
     * 分页响应 - 从列表和总数创建
     *
     * @param list  数据列表
     * @param total 总记录数
     */
    public static <T> Result<PageResult<T>> page(List<T> list, long total) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(),
                PageResult.of(list, total));
    }

    // ==================== 列表响应 (用于下拉选项、简单列表) ====================

    /**
     * 列表响应 - 简单列表，无分页信息
     * 返回格式: [...]
     *
     * @param list 数据列表
     */
    public static <T> Result<List<T>> list(List<T> list) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), list);
    }

    // ==================== 失败响应 ====================

    /**
     * 失败 (使用预定义状态码)
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败 (自定义消息)
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.FAIL.getCode(), message, null);
    }

    /**
     * 失败 (自定义状态码和消息)
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败 (使用预定义状态码 + 自定义消息)
     */
    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    // ==================== 判断方法 ====================

    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}

