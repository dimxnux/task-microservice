package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.DatabaseRepository;
import com.internship.microservice.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ScheduledTaskService {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskService.class);

    private final DatabaseRepository databaseRepository;
    private final RoutingDataSource routingDataSource;
    private final SourceRepository sourceRepository;

    public ScheduledTaskService(DatabaseRepository databaseRepository, RoutingDataSource routingDataSource,
                                SourceRepository sourceRepository) {
        this.databaseRepository = databaseRepository;
        this.routingDataSource = routingDataSource;
        this.sourceRepository = sourceRepository;
    }

    /***
     * Run the scheduled task
     * @throws SQLException if a data source cannot be shut down
     */
    @Scheduled(fixedRateString = "${app.task-execution-delay}")
    private void runScheduledTask() throws SQLException {
        List<Database> targetDatabases = getTargetDatabases();
        routingDataSource.setTargetDataSourcesFromDatabases(targetDatabases);

        for (Database database : targetDatabases) {
            DataSourceContextHolder.setContext(database.getId().toString());

            String currentDatabase = sourceRepository.getCurrentDatabase();
            log.info("Current database: {}", currentDatabase);

            routingDataSource.closeCurrentContextDataSource();
        }
    }

    private List<Database> getTargetDatabases() {
        routingDataSource.setSettingsAsTargetDataSource();
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        return databaseRepository.findAll();
    }
}
