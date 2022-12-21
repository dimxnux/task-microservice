package com.internship.microservice.repository;

import com.internship.microservice.exception.DuplicateUserNameException;
import com.internship.microservice.model.User;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {
    private static final int BATCH_SIZE = 10;

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addUsers(List<User> users) {
        String sqlInsertUser =
                "INSERT INTO users (user_name, first_name, last_name, sex, date_of_birth, nationality, password) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.batchUpdate(sqlInsertUser, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, users.get(i).getUserName());
                    ps.setString(2, users.get(i).getFirstName());
                    ps.setString(3, users.get(i).getLastName());
                    ps.setString(4, users.get(i).getSex());
                    LocalDate userDateOfBirth = users.get(i).getDateOfBirth();
                    if (userDateOfBirth == null) {
                        ps.setNull(5, Types.DATE);
                    } else {
                        ps.setDate(5, Date.valueOf(userDateOfBirth));
                    }
                    ps.setString(6, users.get(i).getNationality());
                    ps.setString(7, users.get(i).getPassword());
                }

                @Override
                public int getBatchSize() {
                    return BATCH_SIZE;
                }
            });
        }

//            int usersCount = 1;
//            for (User user : users) {
//
//
//
//                if (usersCount % BATCH_SIZE == 0) {
//                    preparedStatement.executeBatch();
//                }
//                ++usersCount;
//            }
//            preparedStatement.executeBatch();

    private void checkUserForUsernameDuplicate(User user) {
        Optional<User> foundUser = findByUserName(user.getUserName());
        if (foundUser.isPresent()) {
            throw new DuplicateUserNameException(
                    String.format("The username '%s' is already taken", user.getUserName()));
        }
    }

    public void addUser(User user) {
        String sqlInsertUser =
                "INSERT INTO users (user_name, first_name, last_name, sex, date_of_birth, nationality, password) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlInsertUser,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName(),
                user.getSex(),
                user.getDateOfBirth(),
                user.getNationality(),
                user.getPassword());
    }

    public Optional<User> findByUserName(String userName) {
        String sqlSelectUser =
                "SELECT user_name, first_name, last_name, sex, date_of_birth, nationality, password" +
                        " FROM users WHERE user_name = ?";

        List<User> queriedUsers = jdbcTemplate.query(sqlSelectUser, new BeanPropertyRowMapper<>(User.class), userName);

        return queriedUsers.isEmpty()
                ? Optional.empty()
                : Optional.of(queriedUsers.get(0));
    }
}
