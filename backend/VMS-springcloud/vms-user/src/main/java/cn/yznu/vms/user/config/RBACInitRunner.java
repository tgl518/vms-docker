package cn.yznu.vms.user.config;

import cn.yznu.vms.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * RBAC 缓存初始化
 * 在系统启动时将数据库中的角色权限关系同步到 Redis
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RBACInitRunner implements CommandLineRunner {

    private final RoleService roleService;

    @Override
    public void run(String... args) {
        try {
            roleService.initRolePermissionCache();
        } catch (Exception e) {
            log.error("RBAC 缓存初始化失败", e);
        }
    }
}
