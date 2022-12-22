package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledTaskService {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskService.class);

    private final DatabaseService databaseService;
    private final SourceRepository sourceRepository;

    public ScheduledTaskService(DatabaseService databaseService, SourceRepository sourceRepository) {
        this.databaseService = databaseService;
        this.sourceRepository = sourceRepository;
    }

//    @Scheduled(fixedRateString = "${app.task-execution-delay}")
    private void runScheduledTask() {
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        List<Database> targetDatabases = databaseService.getAllDatabases();

        for (Database database : targetDatabases) {
            DataSourceContextHolder.setContext(database.getName());
            String currentDatabase = sourceRepository.getCurrentDatabase();
            log.info("Current database: {}", currentDatabase);

            DataSourceContextHolder.clearContext();
        }
    }
}
