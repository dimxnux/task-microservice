package com.internship.microservice.config.db;

import com.internship.microservice.model.Database;
import com.internship.microservice.util.DatabaseConverter;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoutingDataSource extends AbstractRoutingDataSource {

    public static final String LOOKUP_KEY_SETTINGS = "settings";

    public static DataSource DATA_SOURCE_SETTINGS;

    @Autowired
    private DatabaseConverter databaseConverter;

    public RoutingDataSource() {
    }

    public void setTargetDataSourcesFromDatabases(Set<Database> databases) {
        Map<Object, Object> dataSources = new HashMap<>();

        for (Database database : databases) {
            DataSource dataSource = databaseConverter.toDataSource(database);
            String lookupKey = String.valueOf(database.getId());
            dataSources.put(lookupKey, dataSource);
        }
        this.setTargetDataSources(dataSources);
        this.afterPropertiesSet();
    }

    public void setSettingsTargetDataSource() {
        Map<Object, Object> settingsTargetDataSource =
                Collections.singletonMap(LOOKUP_KEY_SETTINGS, DATA_SOURCE_SETTINGS);
        this.setTargetDataSources(settingsTargetDataSource);
        this.afterPropertiesSet();
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
