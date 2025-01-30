package com.mangkyu.template.core.infra.cache.cacheable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mangkyu.template.core.infra.cache.GzipRedisSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class CacheableConfig {

    private final ObjectMapper objectMapper;

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new CompositeCacheManager(localCacheManager(), redisCacheManager(redisConnectionFactory));
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

    private CacheManager redisCacheManager(final RedisConnectionFactory redisConnectionFactory) {
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
