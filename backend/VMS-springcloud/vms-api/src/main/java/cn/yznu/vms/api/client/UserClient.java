package cn.yznu.vms.api.client;

import cn.yznu.vms.api.dto.UserDTO;
import cn.yznu.vms.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign Client
 * <p>
 * 其他服务通过此接口调用 vms-user 服务
 * <p>
 * 使用示例：
 *
 * @Autowired private UserClient userClient;
 * <p>
 * Result<UserDTO> result = userClient.getUserById(userId);
 * UserDTO user = result.getData();
 */
@FeignClient(name = "vms-user", path = "/user")
public interface UserClient {

    /**
     * 根据用户ID获取用户信息
     */
    @GetMapping("/info/{userId}")
    Result<UserDTO> getUserById(@PathVariable("userId") Long userId);
}
