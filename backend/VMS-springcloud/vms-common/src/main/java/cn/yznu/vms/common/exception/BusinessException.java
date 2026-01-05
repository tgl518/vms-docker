package cn.yznu.vms.common.exception;

import cn.yznu.vms.common.result.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 * 用于在业务逻辑中抛出可预期的错误，会被全局异常处理器捕获
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 使用状态码枚举创建
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 使用状态码枚举 + 自定义消息创建
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 使用自定义状态码和消息创建
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用消息创建 (默认使用 FAIL 状态码)
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
        this.message = message;
    }
}
