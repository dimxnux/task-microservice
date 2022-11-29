package com.internship.microservice.service;

import com.internship.microservice.model.Database;
import com.internship.microservice.repository.DatabaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    private DatabaseRepository databaseRepository;

    public DatabaseService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public Database addDatabase(Database database) {
        return databaseRepository.save(database);
    }

    public List<Database> getAllDatabases() {
        return databaseRepository.findAll();
    }
}
