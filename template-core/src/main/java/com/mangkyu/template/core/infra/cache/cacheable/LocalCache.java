package com.mangkyu.template.core.infra.cache.cacheable;

import java.time.Duration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocalCache {
    MEMBER(LocalCacheName.MEMBER, Duration.ofMinutes(2).getSeconds(), 200000);

    private final String cacheName;
    private final long expiredAfterWrite;
    private final long maximumSize;

}