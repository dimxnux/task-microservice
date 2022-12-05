package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.SourceRepository;
import com.internship.microservice.util.DatabaseConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScheduledTaskServiceTest {
    @Autowired
    private RoutingDataSource routingDataSource;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DatabaseConverter databaseConverter;

    @Test
    public void testDatabaseSwitchingUsingRoutingDataSource() throws SQLException {
        // switch to settings database
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        List<Database> databases = databaseService.getAllDatabases();

        // switch to test database
        routingDataSource.setTargetDataSourcesFromDatabases(databases);
        Database testDatabase = databases.get(0);
        String testDatabaseId = testDatabase.getId().toString();
        DataSourceContextHolder.setContext(testDatabaseId);

        String currentDatabase = sourceRepository.getCurrentDatabase();

        assertThat(currentDatabase).isEqualTo(getDatabaseName(testDatabase));
    }

    private String getDatabaseName(Database database) throws SQLException {
        DataSource dataSource = databaseConverter.toDataSource(database);
        try (Connection con = dataSource.getConnection()) {
            return con.getCatalog();
        }
    }
}