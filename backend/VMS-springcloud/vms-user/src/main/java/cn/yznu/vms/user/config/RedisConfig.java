package cn.yznu.vms.user.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis缓存配置
 * 配置JSON序列化和缓存过期时间
 */
@Configuration
public class RedisConfig implements CachingConfigurer {

    /**
     * 配置RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用Jackson序列化
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // Key使用String序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value使用JSON序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = createJsonSerializer();

        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))  // 默认30分钟过期
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // 特定缓存配置
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        
        // 用户信息缓存 - 10分钟
        cacheConfigs.put("user", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // 用户权限缓存 - 30分钟
        cacheConfigs.put("user:permissions", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put("userPermissions", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 角色缓存 - 1小时
        cacheConfigs.put("role", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigs.put("roleDetail", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 权限树缓存 - 1小时
        cacheConfigs.put("permission", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigs.put("permissionTree", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigs.put("menuTree", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // 系统配置缓存 - 1小时
        cacheConfigs.put("sysConfig", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }

    /**
     * 创建JSON序列化器
     */
    private GenericJackson2JsonRedisSerializer createJsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(new JavaTimeModule());  // 支持Java 8日期类型
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
