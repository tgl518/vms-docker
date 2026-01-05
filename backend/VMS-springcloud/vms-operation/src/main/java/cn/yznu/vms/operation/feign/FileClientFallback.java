package cn.yznu.vms.operation.feign;

import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 文件服务 Feign 客户端降级处理
 */
@Slf4j
@Component
public class FileClientFallback implements FileClient {

    @Override
    public Result<Void> deleteFile(String url, Long userId) {
        log.warn("文件服务不可用，无法删除文件: {}", url);
        return Result.fail(ResultCode.SERVICE_UNAVAILABLE);
    }
}
