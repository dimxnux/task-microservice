package com.internship.microservice.repository;

import com.internship.microservice.model.Database;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Component
public class DatabaseRepository {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(Database database) {
        String sqlInsert = "INSERT INTO databases (name, url, username, driver_class_name, encrypted_password) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsert,
                database.getName(),
                database.getUrl(),
                database.getUsername(),
                database.getDriverClassName(),
                new SqlParameterValue(Types.BINARY, database.getEncryptedPassword()));
    }

    public List<Database> getAll() {
        String sqlSelect = "SELECT * FROM databases";

        return jdbcTemplate.query(sqlSelect, new BeanPropertyRowMapper<>(Database.class));
    }

    public Optional<Database> getByName(String name) {
        String sqlSelect = "SELECT * FROM databases WHERE name = ?";
        List<Database> queriedDatabases;

        queriedDatabases = jdbcTemplate.query(sqlSelect, new BeanPropertyRowMapper<>(Database.class), name);

        return queriedDatabases.isEmpty()
                ? Optional.empty()
                : Optional.of(queriedDatabases.get(0));
    }

    public void deleteByName(String name) {
        String sqlDelete = "DELETE FROM databases WHERE name = ?";
        jdbcTemplate.update(sqlDelete, name);
    }
}
