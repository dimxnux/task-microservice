package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.exception.DatabaseNotFoundException;
import com.internship.microservice.exception.DuplicateDatabaseNameException;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.DatabaseRepository;
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

    public DatabaseService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
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
        DataSourceContextHolder.clearContext();
    }

    public void deleteDatabaseByName(@NotBlank String databaseName) {
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);

        databaseRepository.getByName(databaseName).orElseThrow(() ->
                new DatabaseNotFoundException(String.format("No database with name '%s' available",
                        databaseName)));

        databaseRepository.deleteByName(databaseName);
        DataSourceContextHolder.clearContext();
    }

    public Optional<Database> getDatabaseByName(@NotBlank String databaseName) {
        return databaseRepository.getByName(databaseName);
    }
}
