package com.mangkyu.template.core.infra.cache.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;

@RequiredArgsConstructor
@Profile("local")
class LocalRedisConfig {

    private GenericContainer<?> redisContainer;

    @PostConstruct
    public void init() {
        int redisPort = 6379;
        redisContainer = new GenericContainer<>("redis:7.2.4");
        redisContainer.withExposedPorts(redisPort);
        redisContainer.start();
        System.setProperty("spring.data.redis.host", redisContainer.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(redisContainer.getMappedPort(redisPort)));
    }

    @PostConstruct
    public void destroy() {
        redisContainer.stop();
    }
}
