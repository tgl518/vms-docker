package cn.yznu.vms.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret = "VmsSpringCloudSecretKeyForJwtTokenGeneration2024";

    /**
     * 过期时间 (毫秒)，默认24小时
     */
    private Long expiration = 86400000L;
}
