package com.internship.microservice.repository;

import com.internship.microservice.exception.DuplicateUserNameException;
import com.internship.microservice.model.User;
import org.hibernate.Session;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {
    private static final long BATCH_SIZE = 10;

    @PersistenceContext
    private EntityManager entityManager;

    public void addUsers(List<User> users) {
        String sqlInsertUser =
                "INSERT INTO users (user_name, first_name, last_name, sex, date_of_birth, nationality, password) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Query query = entityManager.createNativeQuery(sqlInsertUser);

        try (Session session = query.unwrap(Session.class)) {
            session.doWork(connection -> {
                try (PreparedStatement preparedStatement =
                             connection.prepareStatement(sqlInsertUser)) {
                    int usersCount = 1;
                    for (User user : users) {
                        checkUserForUsernameDuplicate(user);

                        preparedStatement.setString(1, user.getUserName());
                        preparedStatement.setString(2, user.getFirstName());
                        preparedStatement.setString(3, user.getLastName());
                        preparedStatement.setString(4, user.getSex());
                        LocalDate userDateOfBirth = user.getDateOfBirth();
                        if (userDateOfBirth == null) {
                            preparedStatement.setNull(5, Types.DATE);
                        } else {
                            preparedStatement.setDate(5, Date.valueOf(userDateOfBirth));
                        }
                        preparedStatement.setString(6, user.getNationality());
                        preparedStatement.setString(7, user.getPassword());
                        preparedStatement.addBatch();

                        if (usersCount % BATCH_SIZE == 0) {
                            preparedStatement.executeBatch();
                        }
                        ++usersCount;
                    }
                    preparedStatement.executeBatch();
                }
            });
        }
    }

    private void checkUserForUsernameDuplicate(User user) {
        Optional<User> foundUser = findByUserName(user.getUserName());
        if (foundUser.isPresent()) {
            throw new DuplicateUserNameException(
                    String.format("The username '%s' is already taken", user.getUserName()));
        }
    }

    public User addUser(User user) {
        String sqlInsertUser =
                "INSERT INTO users (user_name, first_name, last_name, sex, date_of_birth, nationality, password) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "RETURNING user_name, first_name, last_name, sex, date_of_birth, nationality, password";
        Query query = entityManager.createNativeQuery(sqlInsertUser);
        query.setParameter(1, user.getUserName())
                .setParameter(2, user.getFirstName())
                .setParameter(3, user.getLastName())
                .setParameter(4, new TypedParameterValue(StringType.INSTANCE, user.getSex()))
                .setParameter(5, new TypedParameterValue(LocalDateType.INSTANCE, user.getDateOfBirth()))
                .setParameter(6, user.getNationality())
                .setParameter(7, user.getPassword());

        Object[] sqlInsertResult = (Object[]) query.getSingleResult();

        return getUserFromSqlResultArray(sqlInsertResult);
    }

    private User getUserFromSqlResultArray(Object[] sqlResult) {
        String userName = (String) sqlResult[0];
        String firstName = (String) sqlResult[1];
        String lastName = (String) sqlResult[2];
        Object sexValue = sqlResult[3];
        String sex = (sexValue == null) ? null : (String) sexValue;
        Object dateOfBirthValue = sqlResult[4];
        LocalDate dateOfBirth = (dateOfBirthValue == null) ? null : ((Date) dateOfBirthValue).toLocalDate();
        String nationality = (String) sqlResult[5];
        String password = (String) sqlResult[6];

        return new User(firstName, lastName, sex, dateOfBirth, nationality, userName, password);
    }

    public Optional<User> findByUserName(String userName) {
        String sqlFindUser =
                "SELECT user_name, first_name, last_name, sex, date_of_birth, nationality, password" +
                        " FROM users WHERE user_name = ?";
        Query query = entityManager.createNativeQuery(sqlFindUser);
        query.setParameter(1, userName);

        try {
            Object[] sqlSelectResult = (Object[]) query.getSingleResult();

            return Optional.of(getUserFromSqlResultArray(sqlSelectResult));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
