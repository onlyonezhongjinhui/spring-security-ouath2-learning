package io.github.oauth2.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Objects;
import java.util.Random;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

/**
 * @author onlyonezhongjinhui
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Resource
    private RedisConnectionFactory factory;

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(".").append(method.getName());
            if (params.length > 0) {
                sb.append(".");
            }
            for (Object param : params) {
                sb.append(param.toString());
            }
            return sb.toString();
        };
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        return redisTemplate;
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver(Objects.requireNonNull(cacheManager()));
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 用于捕获从Cache中进行CRUD时的异常的回调处理器。
        return new SimpleCacheErrorHandler();
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheConfiguration cacheConfiguration =
                defaultCacheConfig()
                        .disableCachingNullValues()
                        // 默认缓存过期时间8天+随机秒数,防止集中过期
                        .entryTtl(Duration.ofDays(8).plusSeconds(new Random().nextInt(100)))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(factory).cacheDefaults(cacheConfiguration).build();
    }

}
