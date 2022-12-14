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
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final DatabaseService databaseService;

    public UserService(UserRepository userRepository, DatabaseService databaseService) {
        this.userRepository = userRepository;
        this.databaseService = databaseService;
    }

    public void addUsers(@Valid @NotEmpty List<User> users) {
        // Use list as the value type, in order to prevent the loss of users that have the same username,
        // that could lead to a user duplicate exception not being thrown on insertion in the database
        Map<String, List<User>> usersByNationality = users.stream()
                .collect(Collectors.groupingBy(User::getNationality));

        for (Map.Entry<String, List<User>> entry : usersByNationality.entrySet()) {
            List<User> usersOfNationality = entry.getValue();

            if (usersOfNationality.size() > 1) {
                addUsersBatch(usersOfNationality);
            } else {
                addUser(usersOfNationality.get(0));
            }
        }
    }

    private void addUsersBatch(@Valid @NotEmpty List<User> users) {
        String userNationality = users.get(0).getNationality().toLowerCase();
        DataSourceContextHolder.setContext(RoutingDataSource.LOOKUP_KEY_SETTINGS);
        Optional<Database> database = databaseService.getDatabaseByName(userNationality);

        database.orElseThrow(() ->
                new DatabaseNotFoundException(
                        String.format("No database for the users with nationality '%s' available", userNationality))
        );
        DataSourceContextHolder.setContext(userNationality);
        userRepository.addUsers(users);
        DataSourceContextHolder.clearContext();
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
