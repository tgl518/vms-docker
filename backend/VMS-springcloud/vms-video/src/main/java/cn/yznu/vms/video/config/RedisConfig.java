package cn.yznu.vms.video.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
 * Redis 缓存配置（支持 Java8 时间类型）
 */
@Configuration
public class RedisConfig implements CachingConfigurer {

    /**
     * 创建支持 Java8 时间的 ObjectMapper
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册 Java8 时间模块
        mapper.registerModule(new JavaTimeModule());
        // 禁止将日期转为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 禁止空对象报错
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 启用类型信息，确保反序列化时能正确还原类型
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    /**
     * 创建 JSON 序列化器（支持 LocalDateTime）
     */
    private GenericJackson2JsonRedisSerializer jsonSerializer() {
        return new GenericJackson2JsonRedisSerializer(createObjectMapper());
    }

    /**
     * 配置 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 使用 String 序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 使用 JSON 序列化（支持 LocalDateTime）
        GenericJackson2JsonRedisSerializer jsonSerializer = jsonSerializer();
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
        // 默认缓存配置: 30分钟过期
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer()))
                .disableCachingNullValues();

        // 不同缓存的个性化配置
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // 热门视频缓存: 10分钟过期
        cacheConfigs.put("hotVideos", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // 分类列表缓存: 1小时过期
        cacheConfigs.put("categoryList", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 标签列表缓存: 1小时过期
        cacheConfigs.put("tagList", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 视频详情缓存: 30分钟过期
        cacheConfigs.put("videoDetail", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 用户权限缓存: 30分钟过期
        cacheConfigs.put("userPermissions", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
