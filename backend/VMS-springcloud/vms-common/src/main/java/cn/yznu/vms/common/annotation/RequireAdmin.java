package cn.yznu.vms.common.annotation;

import java.lang.annotation.*;

/**
 * 管理员权限校验注解
 * 标记在 Controller 方法或类上，表示需要管理员权限才能访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAdmin {
    
    /**
     * 权限描述，用于日志记录
     */
    String value() default "";
}
