package com.mangkyu.template.core.infra.cache.redis;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.stream.IntStream;

@Profile("local")
@Configuration(proxyBeanMethods = false)
class LocalRedisConfig {

    private final List<GenericContainer<?>> redisNodes;

    public LocalRedisConfig() {
        int redisPort = 6379;

        redisNodes = (List<GenericContainer<?>>) IntStream.range(0, 2)
                .mapToObj(i -> new GenericContainer<>("redis:7.2.4")
                        .withExposedPorts(redisPort)
                        .withCommand("redis-server --cluster-enabled yes --cluster-config-file nodes.conf --cluster-node-timeout 5000")
//                        .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 50))
                ).toList();

        redisNodes.forEach(GenericContainer::start);


        String redisClusterNodes = redisNodes.stream()
                .map(container -> container.getHost() + ":" + container.getMappedPort(redisPort))
                .reduce((a, b) -> a + "," + b)
                .orElseThrow();

        System.setProperty("spring.redis.cluster.nodes", redisClusterNodes);
    }

    @PreDestroy
    public void destroy() {
        redisNodes.forEach(GenericContainer::stop);
    }
}
