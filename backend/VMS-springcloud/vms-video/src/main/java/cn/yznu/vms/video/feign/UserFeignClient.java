package cn.yznu.vms.video.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "vms-user", fallback = UserClientFallback.class)
public interface UserFeignClient {

    /**
     * 获取用户总数
     */
    @GetMapping("/user/count")
    Long getUserCount();

    /**
     * 获取今日新增用户数
     */
    @GetMapping("/user/count/today")
    Long getTodayNewUsers();

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/user/info/{userId}")
    Map<String, Object> getUserInfo(@PathVariable("userId") Long userId);
}

