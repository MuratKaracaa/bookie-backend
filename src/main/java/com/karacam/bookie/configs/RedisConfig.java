package com.karacam.bookie.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    public static final String PRIMARY_REDIS_CONNECTION_FACTORY = "primary_redis_connection_factory";
    public static final String PRIMARY_REDIS_TEMPLATE = "primary_redis_template";
    public static final String PRIMARY_CACHE_MANAGER = "primary_cache_manager";
    public static final String SECONDARY_REDIS_CONNECTION_FACTORY = "secondary_redis_connection_factory";
    public static final String SECONDARY_REDIS_TEMPLATE = "secondary_redis_template";
    public static final String SECONDARY_CACHE_MANAGER = "secondary_cache_manager";

    @Bean(name = PRIMARY_REDIS_CONNECTION_FACTORY)
    @Primary
    public LettuceConnectionFactory primaryRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean(name = PRIMARY_REDIS_TEMPLATE)
    @Primary
    public RedisTemplate<String, Object> primaryRedisTemplate(@Qualifier(PRIMARY_REDIS_CONNECTION_FACTORY) RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        return template;
    }

    @Bean(name = PRIMARY_CACHE_MANAGER)
    @Primary
    public RedisCacheManager primaryCacheManager(@Qualifier(PRIMARY_REDIS_CONNECTION_FACTORY) RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    @Bean(name = SECONDARY_REDIS_CONNECTION_FACTORY)
    public LettuceConnectionFactory secondaryRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6380);
    }

    @Bean(name = SECONDARY_REDIS_TEMPLATE)
    public RedisTemplate<Object, Object> secondaryRedisTemplate(@Qualifier(SECONDARY_REDIS_CONNECTION_FACTORY) RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        return template;
    }

    @Bean(name = SECONDARY_CACHE_MANAGER)
    public RedisCacheManager secondaryCacheManager(@Qualifier(SECONDARY_REDIS_CONNECTION_FACTORY) RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .entryTtl(Duration.ofMinutes(5));
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
}
