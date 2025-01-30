package com.mangkyu.template.core.infra.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
class CacheConfig {

}
