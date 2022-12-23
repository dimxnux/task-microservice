package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.event.DatabasesUpdateEvent;
import com.internship.microservice.exception.DatabaseNotFoundException;
import com.internship.microservice.exception.DuplicateDatabaseNameException;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.DatabaseRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class DatabaseService {
    private final DatabaseRepository databaseRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public DatabaseService(DatabaseRepository databaseRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.databaseRepository = databaseRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<Database> getAllDatabases() {
        return databaseRepository.getAll();
    }

    public void addDatabase(@Valid Database database) {
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        Optional<Database> foundDatabase = databaseRepository.getByName(database.getName());
        if (foundDatabase.isPresent()) {
            throw new DuplicateDatabaseNameException(String.format("The database name '%s' is already taken",
                    foundDatabase.get().getName()));
        }

        databaseRepository.add(database);
        refreshTargetDataSources();
        DataSourceContextHolder.clearContext();
    }

    public void deleteDatabaseByName(@NotBlank String databaseName) {
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        databaseRepository.getByName(databaseName).orElseThrow(() ->
                new DatabaseNotFoundException(String.format("No database with name '%s' available",
                        databaseName)));

        databaseRepository.deleteByName(databaseName);
        refreshTargetDataSources();
        DataSourceContextHolder.clearContext();
    }

    public void deleteAllDatabases() {
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        databaseRepository.deleteAll();
        refreshTargetDataSources();
        DataSourceContextHolder.clearContext();
    }

    public Optional<Database> getDatabaseByName(@NotBlank String databaseName) {
        return databaseRepository.getByName(databaseName);
    }

    private void refreshTargetDataSources() {
        applicationEventPublisher.publishEvent(new DatabasesUpdateEvent());
    }
}
