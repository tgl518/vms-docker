package cn.yznu.vms.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 标记在 Controller 方法或类上，表示需要具有特定权限编码才能访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 权限编码，例如 "user:add", "video:delete"
     */
    String value();
    
    /**
     * 权限描述，用于日志记录
     */
    String description() default "";
}
