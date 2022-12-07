package com.internship.microservice.controller;

import com.internship.microservice.dto.DatabaseDTO;
import com.internship.microservice.service.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DatabaseController {
    private final DatabaseService databaseService;
    private final BytesEncryptor bytesEncryptor;

    public DatabaseController(DatabaseService databaseService, BytesEncryptor bytesEncryptor) {
        this.databaseService = databaseService;
        this.bytesEncryptor = bytesEncryptor;
    }

    @PostMapping("database/new")
    public ResponseEntity<?> addDatabase(@RequestBody @Valid DatabaseDTO databaseDTO) {
        databaseService.addDatabase(databaseDTO.toDatabase(bytesEncryptor));

        return ResponseEntity.ok()
                .build();
    }
}