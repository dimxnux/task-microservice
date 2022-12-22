package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.model.Database;
import com.internship.microservice.repository.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScheduledTaskServiceTest {
    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    public void testDatabaseSwitchingUsingRoutingDataSource() {
        // switch to settings database
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        List<Database> databases = databaseService.getAllDatabases();

        // switch to test database
        Database testDatabase = databases.get(0);
        String testDatabaseName = testDatabase.getName();
        DataSourceContextHolder.setContext(testDatabaseName);

        String currentDatabase = sourceRepository.getCurrentDatabase();

        assertThat(currentDatabase).isEqualTo(testDatabaseName);
    }
}