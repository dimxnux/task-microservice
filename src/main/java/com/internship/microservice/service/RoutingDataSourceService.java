package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.util.DatabaseConverter;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

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

    /**
     * Replace all data sources from the {@code targetDataSources} with the new list of data sources
     * fetched from the "settings". Doesn't replace the "settings" data source, because its
     * only instance is stored in the {@link RoutingDataSource}.
     */
    public void refreshTargetDataSources() {
        Map<Object, Object> targetDataSources = getCandidateTargetDataSources();
        closeTargetDataSources();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
    }

    /**
     * Close all data sources from the targetDataSources except the "settings" data source.
     */
    private void closeTargetDataSources() {
        Map<Object, DataSource> unmodifiableDataSources = routingDataSource.getResolvedDataSources();
        Map<Object, DataSource> dataSources = new HashMap<>(unmodifiableDataSources);
        dataSources.remove(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        for (DataSource dataSource : dataSources.values()) {
            ((AtomikosDataSourceBean) dataSource).close();
        }
    }
}
