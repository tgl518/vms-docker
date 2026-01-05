package cn.yznu.vms.video.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务降级处理
 */
@Slf4j
@Component
public class UserClientFallback implements UserFeignClient {

    @Override
    public Long getUserCount() {
        log.warn("获取用户总数失败，返回默认值0");
        return 0L;
    }

    @Override
    public Long getTodayNewUsers() {
        log.warn("获取今日新增用户数失败，返回默认值0");
        return 0L;
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        log.warn("获取用户信息失败，返回空Map");
        return new HashMap<>();
    }
}

