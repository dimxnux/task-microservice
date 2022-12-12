package com.internship.microservice.config.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.SQLException;

public class RoutingDataSource extends AbstractRoutingDataSource {
    public static final String LOOKUP_KEY_SETTINGS = "settings";

    public static DataSource DATA_SOURCE_SETTINGS;

    public RoutingDataSource() {
    }

    public void closeCurrentContextDataSource() throws SQLException {
        if (DataSourceContextHolder.getContext() == null) {
            throw new RuntimeException("DataSource context is null");
        }
        DataSource dataSource = this.determineTargetDataSource();
        dataSource.unwrap(HikariDataSource.class)
                .close();
    }

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getContext();
    }
}
