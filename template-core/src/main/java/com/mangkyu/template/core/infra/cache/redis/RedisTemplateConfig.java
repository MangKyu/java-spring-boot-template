package com.mangkyu.template.core.infra.cache.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangkyu.template.core.domain.member.Member;
import com.mangkyu.template.core.infra.cache.GzipRedisSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
public class RedisTemplateConfig {

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

}
