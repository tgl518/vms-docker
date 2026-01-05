package cn.yznu.vms.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
     * 过期时间 (毫秒)
     */
    private Long expiration = 86400000L;

    /**
     * 不需要认证的 URL 列表
     */
    private List<String> ignoreUrls = new ArrayList<>();
}
