package com.internship.microservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SourceRepository {
    private final JdbcTemplate jdbcTemplate;

    public SourceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getCurrentDatabase() {
        // should return null?
        return jdbcTemplate.queryForObject("SELECT current_database()",
                (rs, rowNum) -> rs.getString(1));
    }
}
