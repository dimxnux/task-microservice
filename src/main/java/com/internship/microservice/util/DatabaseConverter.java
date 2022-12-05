package com.internship.microservice.util;

import com.internship.microservice.model.Database;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseConverter {
    private final BytesEncryptor encryptor;

    public DatabaseConverter(BytesEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public DataSource toDataSource(Database database) {
        return DataSourceBuilder.create()
                .url(database.getUrl())
                .username(database.getUsername())
                .password(database.decryptPassword(encryptor))
                .driverClassName(database.getDriverClassName())
                .type(HikariDataSource.class)
                .build();
    }
}
