package com.mangkyu.template.core.infra.cache.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

@RequiredArgsConstructor
@Import(LocalRedisConfig.class)
public class LettuceConfig {

}
