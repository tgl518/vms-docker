package cn.yznu.vms.video.feign;

import cn.yznu.vms.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文件服务 Feign 客户端
 * 用于调用 vms-file 服务的文件删除接口
 */
@FeignClient(name = "vms-file", fallback = FileClientFallback.class)
public interface FileClient {

    /**
     * 删除文件
     *
     * @param url    文件的完整 URL
     * @param userId 用户ID（用于安全校验）
     * @return 删除结果
     */
    @DeleteMapping("/file")
    Result<Void> deleteFile(
            @RequestParam("url") String url,
            @RequestHeader(value = "X-User-Id", required = false) Long userId
    );
}
