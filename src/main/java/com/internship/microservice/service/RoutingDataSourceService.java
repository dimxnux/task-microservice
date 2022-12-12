package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.util.DatabaseConverter;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoutingDataSourceService {
    private final RoutingDataSource routingDataSource;
    private final DatabaseService databaseService;
    private final DatabaseConverter databaseConverter;

    public RoutingDataSourceService(RoutingDataSource routingDataSource, DatabaseService databaseService,
                                    DatabaseConverter databaseConverter) {
        this.routingDataSource = routingDataSource;
        this.databaseService = databaseService;
        this.databaseConverter = databaseConverter;
    }

    private Map<Object, Object> getCandidateTargetDataSources() {
        Map<Object, Object> candidateTargetDataSources = new HashMap<>();
        candidateTargetDataSources.put(RoutingDataSource.LOOKUP_KEY_SETTINGS, RoutingDataSource.DATA_SOURCE_SETTINGS);

        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        List<Database> databases = databaseService.getAllDatabases();
        for (Database database : databases) {
            String lookupKey = database.getName();
            DataSource dataSource = databaseConverter.toDataSource(database);
            candidateTargetDataSources.put(lookupKey, dataSource);
        }

        return candidateTargetDataSources;
    }

    public void refreshTargetDataSources() {
        Map<Object, Object> targetDataSources = getCandidateTargetDataSources();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
    }
}
