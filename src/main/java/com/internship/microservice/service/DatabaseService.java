package com.internship.microservice.service;

import com.internship.microservice.model.Database;
import com.internship.microservice.repository.DatabaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {
    private final DatabaseRepository databaseRepository;

    public DatabaseService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public List<Database> getAllDatabases() {
        return databaseRepository.findAll();
    }
}
