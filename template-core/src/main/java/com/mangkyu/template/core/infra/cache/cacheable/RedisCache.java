package com.mangkyu.template.core.infra.cache.cacheable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mangkyu.template.core.domain.member.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import static com.mangkyu.template.core.infra.cache.cacheable.RedisCacheName.MEMBER_CACHE_NAME;

@Getter
@RequiredArgsConstructor
public enum RedisCache {
    MEMBER(MEMBER_CACHE_NAME, Duration.ofMinutes(30), new TypeReference<Member>() {}),
    ;

    private final String cacheName;
    private final Duration expiredAfterWrite;
    private final TypeReference<?> typeRef;

}