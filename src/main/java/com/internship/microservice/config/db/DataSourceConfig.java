package com.internship.microservice.config.db;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(SettingsDataSourceProperties properties) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        RoutingDataSource.DATA_SOURCE_SETTINGS = DataSourceBuilder.create()
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();

        Map<Object, Object> targetDataSources =
                Collections.singletonMap(RoutingDataSource.LOOKUP_KEY_SETTINGS, RoutingDataSource.DATA_SOURCE_SETTINGS);
        routingDataSource.setTargetDataSources(targetDataSources);

        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        return routingDataSource;
    }
}
