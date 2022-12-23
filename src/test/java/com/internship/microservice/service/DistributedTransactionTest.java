package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.dto.DatabaseDTO;
import com.internship.microservice.exception.DatabaseNotFoundException;
import com.internship.microservice.model.Database;
import com.internship.microservice.model.User;
import com.internship.microservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class DistributedTransactionTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private BytesEncryptor bytesEncryptor;

    @BeforeEach
    public void setUp() {
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        databaseService.deleteAllDatabases();
        databaseService.addDatabase(getMDDatabase());
        databaseService.addDatabase(getUSDatabase());

        DataSourceContextHolder.setContext("md_test");
        userRepository.deleteAll();
        DataSourceContextHolder.setContext("us_test");
        userRepository.deleteAll();

        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        databaseService.deleteAllDatabases();
        databaseService.addDatabase(getMDDatabase());
        DataSourceContextHolder.clearContext();
    }

    @Test
    void givenMdDatabase_whenAddUsersToMdAndUsDatabases_thenExceptionAndRollbackMdDatabase() {
        List<User> users = getValidUsersFromMdAndUs();

        assertThatThrownBy(() -> userService.addUsers(users))
                .hasCauseInstanceOf(DatabaseNotFoundException.class);

        DataSourceContextHolder.setContext("md_test");
        List<User> mdUsers = userRepository.getAll();

        assertThat(mdUsers).isEmpty();
    }

    @Test
    void givenRollbackDueToMissingUsDatabase_whenAddUsDatabaseAndTryAgain_thenSuccess() {
        List<User> users = getValidUsersFromMdAndUs();

        // rollback on missing US database
        assertThatThrownBy(() -> userService.addUsers(users));

        databaseService.addDatabase(getUSDatabase());

        // repeat and expect commit to both databases
        userService.addUsers(users);

        DataSourceContextHolder.setContext("md_test");
        List<User> mdUsers = userRepository.getAll();

        DataSourceContextHolder.setContext("us_test");
        List<User> usUsers = userRepository.getAll();

        assertThat(mdUsers).hasSize(2);
        assertThat(usUsers).hasSize(2);

    }

    private Database getMDDatabase() {
        return new DatabaseDTO("md_test", "jdbc:postgresql://localhost:5432/md_test",
                "postgres", "org.postgresql.Driver", "root").toDatabase(bytesEncryptor);
    }

    private Database getUSDatabase() {
        return new DatabaseDTO("us_test", "jdbc:postgresql://localhost:5432/us_test",
                "postgres", "org.postgresql.Driver", "root").toDatabase(bytesEncryptor);
    }


    private List<User> getValidUsersFromMdAndUs() {
        return Arrays.asList(
                new User("mdUser1", "mdUser1", "M", LocalDate.now(),
                        "MD_test", "user1-md", "123"),
                new User("mdUser2", "mdUser2", "F", LocalDate.now(),
                        "MD_test", "user2-md", "123"),
                new User("usUser1", "usUser1", "F", LocalDate.now(),
                        "US_test", "user1-us", "123"),
                new User("usUser2", "usUser2", "F", LocalDate.now(),
                        "US_test", "user2-us", "123")
        );
    }
}
