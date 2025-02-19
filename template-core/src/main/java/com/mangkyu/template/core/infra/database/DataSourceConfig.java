package com.mangkyu.template.core.infra.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.Map;

// https://vladmihalcea.com/read-write-read-only-transaction-routing-spring/
@Configuration(proxyBeanMethods = false)
class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("writeDataSource")
            DataSource writeDataSource,
            @Qualifier("readOnlyDataSource")
            DataSource readOnlyDataSource
    ) {
        AnnotationRoutingDataSource routingDataSource = new AnnotationRoutingDataSource();
        Map<Object, Object> dataSourceMap = Map.of(
                DataSourceRoute.RW, writeDataSource,
                DataSourceRoute.RO, readOnlyDataSource
        );

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);
        routingDataSource.afterPropertiesSet();
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean("writeDataSource")
    public DataSource writeDataSource(@Qualifier("writeHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean("writeHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public HikariConfig writeHikariConfig() {
        return new HikariConfig();
    }

    @Bean("readOnlyDataSource")
    public DataSource readOnlyDataSource(@Qualifier("readOnlyHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean("readOnlyHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.read-only")
    public HikariConfig readOnlyHikariConfig() {
        return new HikariConfig();
    }

}
