package cn.yznu.vms.common.interceptor;

import cn.yznu.vms.common.annotation.RequireAdmin;
import cn.yznu.vms.common.annotation.RequireLogin;
import cn.yznu.vms.common.annotation.RequirePermission;
import cn.yznu.vms.common.exception.ForbiddenException;
import cn.yznu.vms.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 权限校验拦截器
 * 升级版：支持基于权限编码的动态 RBAC 校验
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    private final StringRedisTemplate redisTemplate;

    public AuthInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String REDIS_ROLE_PERMISSION_PREFIX = "role_permissions:";
    private static final String ADMIN_ROLE = "admin";
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String userId = request.getHeader(USER_ID_HEADER);
        String userRolesHeader = request.getHeader(USER_ROLE_HEADER);

        // 1. 获取权限校验注解
        RequirePermission requirePermission = getAnnotation(handlerMethod, RequirePermission.class);
        RequireAdmin requireAdmin = getAnnotation(handlerMethod, RequireAdmin.class);
        RequireLogin requireLogin = getAnnotation(handlerMethod, RequireLogin.class);

        // 如果没有任何权限要求，直接放行
        if (requirePermission == null && requireAdmin == null && requireLogin == null) {
            return true;
        }

        // 2. 检查登录状态
        if (!StringUtils.hasText(userId)) {
            throw new UnauthorizedException("请先登录");
        }

        // 3. 执行特定权限/角色校验
        List<String> userRoles = StringUtils.hasText(userRolesHeader) 
                ? Arrays.asList(userRolesHeader.split(",")) 
                : Arrays.asList("user");

        // 3.1 超级管理员（SUPER_ADMIN）拥有所有权限，直接过
        if (userRoles.contains(SUPER_ADMIN_ROLE)) {
            return true;
        }

        // 3.2 校验管理员权限 (@RequireAdmin)
        if (requireAdmin != null) {
            if (!userRoles.contains(ADMIN_ROLE)) {
                log.warn("越权访问管理员接口: userId={}, roles={}, uri={}", userId, userRolesHeader, request.getRequestURI());
                throw new ForbiddenException("需要管理员权限");
            }
        }

        // 3.3 校验功能权限 (@RequirePermission)
        if (requirePermission != null) {
            String permissionCode = requirePermission.value();
            boolean hasPermission = false;
            
            for (String role : userRoles) {
                // 从 Redis 缓存获取角色的权限列表 (Set 结构)
                Set<String> permissions = redisTemplate.opsForSet().members(REDIS_ROLE_PERMISSION_PREFIX + role);
                if (permissions != null && permissions.contains(permissionCode)) {
                    hasPermission = true;
                    break;
                }
            }

            if (!hasPermission) {
                log.warn("权限不足: userId={}, roles={}, requiredPermission={}, uri={}", 
                        userId, userRolesHeader, permissionCode, request.getRequestURI());
                throw new ForbiddenException("权限不足，缺少: " + permissionCode);
            }
        }

        return true;
    }

    private <A extends java.lang.annotation.Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        A annotation = handlerMethod.getMethodAnnotation(annotationType);
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(annotationType);
        }
        return annotation;
    }
}
