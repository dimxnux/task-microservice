package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.exception.DatabaseNotFoundException;
import com.internship.microservice.exception.DuplicateDatabaseAliasException;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.DatabaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@Service
@Validated
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

        Optional<Database> foundDatabase = databaseRepository.findByAlias(database.getAlias());
        if (foundDatabase.isPresent()) {
            throw new DuplicateDatabaseAliasException(String.format("The database alias '%s' is already taken",
                    foundDatabase.get().getAlias()));
        }

        databaseRepository.save(database);
        DataSourceContextHolder.clearContext();
    }

    public void deleteDatabaseById(@Min(1) Long databaseId) {
        routingDataSource.setSettingsAsTargetDataSource();
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        databaseRepository.findById(databaseId).orElseThrow(() ->
                        new DatabaseNotFoundException(String.format("No database with id '%d' available", databaseId)));

        databaseRepository.deleteById(databaseId);
        DataSourceContextHolder.clearContext();
    }
}
