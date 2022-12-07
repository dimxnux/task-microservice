package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.DatabaseRepository;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public class DatabaseService {
    private final DatabaseRepository databaseRepository;
    private final RoutingDataSource routingDataSource;

    public DatabaseService(DatabaseRepository databaseRepository, RoutingDataSource routingDataSource) {
        this.databaseRepository = databaseRepository;
        this.routingDataSource = routingDataSource;
    }

    public List<Database> getAllDatabases() {
        return databaseRepository.findAll();
    }

    public void addDatabase(@Valid Database database) {
        routingDataSource.setSettingsAsTargetDataSource();
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        databaseRepository.save(database);
        DataSourceContextHolder.clearContext();
    }
}
