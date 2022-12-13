package com.internship.microservice.service;

import com.internship.microservice.config.db.DataSourceContextHolder;
import com.internship.microservice.config.db.RoutingDataSource;
import com.internship.microservice.exception.DatabaseNotFoundException;
import com.internship.microservice.exception.DuplicateUserNameException;
import com.internship.microservice.model.Database;
import com.internship.microservice.model.User;
import com.internship.microservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final DatabaseService databaseService;

    public UserService(UserRepository userRepository, DatabaseService databaseService) {
        this.userRepository = userRepository;
        this.databaseService = databaseService;
    }

    public void addUsers(@Valid List<User> users) {
        for (User user : users) {
            addUser(user);
        }
    }

    public void addUser(User user) {
        String userNationality = user.getNationality().toLowerCase();
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        Optional<Database> database = databaseService.getDatabaseByName(userNationality);

        database.orElseThrow(() ->
                new DatabaseNotFoundException(
                        String.format("No database for the users with nationality '%s' available", userNationality))
        );

        DataSourceContextHolder.setContext(userNationality);
        Optional<User> foundUser = userRepository.findByUserName(user.getUserName());
        if (foundUser.isPresent()) {
            throw new DuplicateUserNameException(
                    String.format("The username '%s' is already taken", user.getUserName()));
        }

        userRepository.addUser(user);
        DataSourceContextHolder.clearContext();
    }
}
