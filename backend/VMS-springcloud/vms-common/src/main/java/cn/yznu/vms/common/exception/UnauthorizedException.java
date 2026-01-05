package cn.yznu.vms.common.exception;

import cn.yznu.vms.common.result.ResultCode;

/**
 * 未登录异常
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED.getCode(), message);
    }
}
