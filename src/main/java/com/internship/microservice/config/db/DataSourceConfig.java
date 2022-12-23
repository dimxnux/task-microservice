package com.internship.microservice.config.db;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
public class DataSourceConfig {
    @Value("${app.jta.atomikos.pool-size}")
    private int poolSize;

    @Bean
    public RoutingDataSource dataSource(SettingsDataSourceProperties properties) {
        PGXADataSource xaDataSource = new PGXADataSource();
        xaDataSource.setUrl(properties.getUrl());
        xaDataSource.setUser(properties.getUsername());
        xaDataSource.setPassword(properties.getPassword());

        AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
        atomikosDataSource.setXaDataSource(xaDataSource);
        atomikosDataSource.setPoolSize(poolSize);
        atomikosDataSource.setUniqueResourceName(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        RoutingDataSource.DATA_SOURCE_SETTINGS = atomikosDataSource;
        Map<Object, Object> targetDataSources =
                Collections.singletonMap(RoutingDataSource.LOOKUP_KEY_SETTINGS, RoutingDataSource.DATA_SOURCE_SETTINGS);
        routingDataSource.setTargetDataSources(targetDataSources);
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        return routingDataSource;
    }
}
