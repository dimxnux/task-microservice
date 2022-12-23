package com.internship.microservice.repository;

import com.internship.microservice.exception.DuplicateUserNameException;
import com.internship.microservice.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {
    private static final int BATCH_SIZE = 10;
    private static final String ALL_USER_COLUMNS =
            "user_name, first_name, last_name, sex, date_of_birth, nationality, password";
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addUsers(List<User> users) {
        String sqlInsertUser =
                "INSERT INTO users (" + ALL_USER_COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> list = new ArrayList<>();
        int i = 1;
        for (User user : users) {
            checkUserForUsernameDuplicate(user);
            list.add(mapUserToObjectArray(user));

            if (i % BATCH_SIZE == 0) {
                jdbcTemplate.batchUpdate(sqlInsertUser, list);
                list.clear();
            }
            ++i;
        }
        jdbcTemplate.batchUpdate(sqlInsertUser, list);
    }

    private Object[] mapUserToObjectArray(User user) {
        return new Object[]{
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getSex(),
                user.getDateOfBirth(),
                user.getNationality(),
                user.getPassword()
        };
    }

    private void checkUserForUsernameDuplicate(User user) {
        Optional<User> foundUser = findByUserName(user.getUserName());
        if (foundUser.isPresent()) {
            throw new DuplicateUserNameException(
                    String.format("The username '%s' is already taken", user.getUserName()));
        }
    }

    public void addUser(User user) {
        String sqlInsertUser =
                "INSERT INTO users (" + ALL_USER_COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        Object[] userFields = mapUserToObjectArray(user);
        jdbcTemplate.update(sqlInsertUser, userFields);
    }

    public List<User> getAll() {
        String sqlSelect = "SELECT " + ALL_USER_COLUMNS + " FROM users";

        return jdbcTemplate.query(sqlSelect, new BeanPropertyRowMapper<>(User.class));
    }

    public Optional<User> findByUserName(String userName) {
        String sqlSelectUser =
                "SELECT " + ALL_USER_COLUMNS + " FROM users WHERE user_name = ?";

        List<User> queriedUsers = jdbcTemplate.query(sqlSelectUser, new BeanPropertyRowMapper<>(User.class), userName);

        return queriedUsers.isEmpty()
                ? Optional.empty()
                : Optional.of(queriedUsers.get(0));
    }

    public void deleteAll() {
        String sqlDelete = "TRUNCATE users";
        jdbcTemplate.update(sqlDelete);
    }
}
