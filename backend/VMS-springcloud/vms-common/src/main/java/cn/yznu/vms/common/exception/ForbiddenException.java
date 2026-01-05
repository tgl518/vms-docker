package cn.yznu.vms.common.exception;

import cn.yznu.vms.common.result.ResultCode;

/**
 * 权限不足异常
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super(ResultCode.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(ResultCode.FORBIDDEN.getCode(), message);
    }
}
