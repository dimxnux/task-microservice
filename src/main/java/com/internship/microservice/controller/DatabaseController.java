package com.internship.microservice.controller;

import com.internship.microservice.dto.DatabaseDTO;
import com.internship.microservice.service.DatabaseService;
import com.internship.microservice.service.RoutingDataSourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class DatabaseController {
    private final DatabaseService databaseService;
    private final BytesEncryptor bytesEncryptor;
    private final RoutingDataSourceService routingDataSourceService;

    public DatabaseController(DatabaseService databaseService, BytesEncryptor bytesEncryptor,
                              RoutingDataSourceService routingDataSourceService) {
        this.databaseService = databaseService;
        this.bytesEncryptor = bytesEncryptor;
        this.routingDataSourceService = routingDataSourceService;
    }

    @PostMapping("database/new")
    public ResponseEntity<?> addDatabase(@RequestBody @Valid DatabaseDTO databaseDTO) {
        databaseService.addDatabase(databaseDTO.toDatabase(bytesEncryptor));
        routingDataSourceService.refreshTargetDataSources();

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("database")
    public ResponseEntity<?> deleteDatabase(@RequestParam("name") String databaseName) {
        databaseService.deleteDatabaseByName(databaseName);
        routingDataSourceService.refreshTargetDataSources();

        return ResponseEntity.ok()
                .build();
    }
}