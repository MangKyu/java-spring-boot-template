package com.mangkyu.template.core.infra.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mangkyu.template.core.domain.member.Member;
import com.mangkyu.template.core.shared.TemplateCoreConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@EnableCaching
@RequiredArgsConstructor
public class CacheConfig implements TemplateCoreConfig {

    private final RedisConnectionFactory redisConnectionFactory;
    private final ObjectMapper objectMapper;

    @Bean
    public RedisTemplate<String, Member> memberRedisTemplate() {
        return createGzipJsonRedisTemplate(objectMapper, new TypeReference<>() {});
    }

    private <V> RedisTemplate<String, V> createGzipJsonRedisTemplate(
        ObjectMapper objectMapper,
        TypeReference<V> typeRef
    ) {
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GzipRedisSerializer<>(objectMapper, typeRef));
        return redisTemplate;
    }

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new CompositeCacheManager(localCacheManager(), redisCacheManager());
    }

    private CacheManager localCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
            Arrays.stream(LocalCache.values())
                .map(cache -> new CaffeineCache(
                    cache.getCacheName(),
                    Caffeine.newBuilder()
                        .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.SECONDS)
                        .maximumSize(cache.getMaximumSize())
                        .recordStats()
                        .build()
                ))
                .collect(Collectors.toList())
        );
        return cacheManager;
    }

    private CacheManager redisCacheManager() {
        Map<String, RedisCacheConfiguration> cacheConfigMap = Arrays.stream(RedisCache.values())
            .collect(Collectors.toMap(
                RedisCache::getCacheName,
                redisCache -> RedisCacheConfiguration.defaultCacheConfig()
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GzipRedisSerializer<>(objectMapper, redisCache.getTypeRef(), 1024, 4096)))
                    .disableCachingNullValues()
                    .entryTtl(redisCache.getExpiredAfterWrite())
                    .prefixCacheNameWith("family-promotion:")
            ));

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .withInitialCacheConfigurations(cacheConfigMap)
            .build();
    }
}
