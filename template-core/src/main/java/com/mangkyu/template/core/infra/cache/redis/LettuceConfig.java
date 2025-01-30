package com.mangkyu.template.core.infra.cache.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.metrics.CommandLatencyCollector;
import io.lettuce.core.metrics.CommandLatencyId;
import io.lettuce.core.metrics.CommandMetrics;
import io.lettuce.core.protocol.ProtocolKeyword;
import io.lettuce.core.resource.DefaultClientResources;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.redis.LettuceMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.ClientResourcesBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.net.SocketAddress;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RedisAutoConfiguration.class)
@AutoConfigureAfter({LettuceMetricsAutoConfiguration.class})
class LettuceConfig {

    private final RedisProperties redisProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean(initMethod = "initConnection")
    public LettuceConnectionFactory lettuceConnectionFactory(DefaultClientResources defaultClientResources) {
//        List<String> nodes = Arrays.stream(redisProperties.getHost().split(",")).toList();
        List<String> nodes = Arrays.stream(System.getProperty("spring.redis.cluster.nodes").split(",")).toList();
//        List<String> nodes = List.of("localhost:6379");
        return lettuceConnectionFactory(nodes, redisProperties.getPassword(), clientConfiguration(defaultClientResources));
    }

    private LettuceConnectionFactory lettuceConnectionFactory(List<String> clusterNodes, String password, LettuceClientConfiguration clientConfiguration) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
//        redisClusterConfiguration.setUsername(redisProperties.getUsername());
//        redisClusterConfiguration.setPassword("");

        return new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
    }

    public LettuceClientConfiguration clientConfiguration(DefaultClientResources resources) {
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(30))
                .enableAllAdaptiveRefreshTriggers()
                .dynamicRefreshSources(false)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(3))
                .build();

        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofSeconds(3))
                .tcpNoDelay(true)
                .build();

        ClientOptions clientOptions = ClusterClientOptions.builder()
                .timeoutOptions(TimeoutOptions.enabled())
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .socketOptions(socketOptions)
                .build();

        return LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(30))
                .shutdownTimeout(Duration.ZERO)
                .clientResources(resources)
                .clientOptions(clientOptions)
                .readFrom(ReadFrom.MASTER)
                .clientName(applicationName)
                .build();
    }

    @Bean
    public ClientResourcesBuilderCustomizer clientResourcesBuilderCustomizer(MeterRegistry meterRegistry) {
        return clientResourcesBuilder -> {
            DefaultEventPublisherOptions latencyPublisherOptions = DefaultEventPublisherOptions.builder()
                    .eventEmitInterval(Duration.ofSeconds(20)).build();

            clientResourcesBuilder.commandLatencyPublisherOptions(latencyPublisherOptions);
            clientResourcesBuilder.ioThreadPoolSize(16);
            clientResourcesBuilder.computationThreadPoolSize(16);
            clientResourcesBuilder.commandLatencyRecorder(new RedisLatencyRecorder(meterRegistry));
        };
    }

    private static class RedisLatencyRecorder implements CommandLatencyCollector {

        private static final Set<String> EXCLUDE_COMMANDS = Set.of("AUTH", "CLIENT", "HELLO", "INFO", "PING", "CLUSTER");
        private final MeterRegistry meterRegistry;
        private final double[] percentiles;

        public RedisLatencyRecorder(final MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
            percentiles = new double[]{0.5, 0.95};
        }

        @Override
        public void recordCommandLatency(
                final SocketAddress local,
                final SocketAddress remote,
                final ProtocolKeyword commandType,
                final long firstResponseLatency,
                final long completionLatency
        ) {
            if (EXCLUDE_COMMANDS.contains(commandType.name())) {
                return;
            }

            // TODO;
        }


        @Override
        public void shutdown() {

        }

        @Override
        public Map<CommandLatencyId, CommandMetrics> retrieveMetrics() {
            return Map.of();
        }
    }
}


