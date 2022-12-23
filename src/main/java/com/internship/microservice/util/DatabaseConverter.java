package com.internship.microservice.util;

import com.internship.microservice.model.Database;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseConverter {
    @Value("${app.jta.atomikos.pool-size}")
    private int poolSize;

    private final BytesEncryptor encryptor;

    public DatabaseConverter(BytesEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public DataSource toDataSource(Database database) {
        PGXADataSource xaDataSource = new PGXADataSource();
        xaDataSource.setURL(database.getUrl());
        xaDataSource.setUser(database.getUsername());
        xaDataSource.setPassword(database.decryptPassword(encryptor));

        AtomikosDataSourceBean atomikosDataSource = new AtomikosDataSourceBean();
        atomikosDataSource.setXaDataSource(xaDataSource);
        atomikosDataSource.setPoolSize(poolSize);
        atomikosDataSource.setUniqueResourceName(database.getName());

        return atomikosDataSource;
    }
}
