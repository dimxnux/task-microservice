package com.internship.microservice.repository;

import com.internship.microservice.model.User;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class UserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public User addUser(User user) {
        String insertUserSql =
                "INSERT INTO users (first_name, last_name, sex, date_of_birth, nationality, user_name, password) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "RETURNING first_name, last_name, sex, date_of_birth, nationality, user_name, password";
        Query query = entityManager.createNativeQuery(insertUserSql);
        query.setParameter(1, user.getFirstName())
                .setParameter(2, user.getLastName())
                .setParameter(3, new TypedParameterValue(StringType.INSTANCE, user.getSex()))
                .setParameter(4, new TypedParameterValue(LocalDateType.INSTANCE, user.getDateOfBirth()))
                .setParameter(5, user.getNationality())
                .setParameter(6, user.getUserName())
                .setParameter(7, user.getPassword());
        Object[] sqlInsertResult = (Object[]) query.getSingleResult();

        return getUserFromSqlResultArray(sqlInsertResult);
    }

    private User getUserFromSqlResultArray(Object[] sqlResult) {
        String firstName = (String) sqlResult[0];
        String lastName = (String) sqlResult[1];
        Object sexValue = sqlResult[2];
        String sex = (sexValue == null) ? null : (String) sexValue;
        Object dateOfBirthValue = sqlResult[3];
        LocalDate dateOfBirth = (dateOfBirthValue == null) ? null : ((Date) dateOfBirthValue).toLocalDate();
        String nationality = (String) sqlResult[4];
        String userName = (String) sqlResult[5];
        String password = (String) sqlResult[6];

        return new User(firstName, lastName, sex, dateOfBirth, nationality, userName, password);
    }

    public Optional<User> findByUserName(String userName) {
        String findUserSql =
                "SELECT first_name, last_name, sex, date_of_birth, nationality, user_name, password" +
                        " FROM users WHERE user_name = ?";
        Query query = entityManager.createNativeQuery(findUserSql);
        query.setParameter(1, userName);

        try {
            Object[] sqlSelectResult = (Object[]) query.getSingleResult();

            return Optional.of(getUserFromSqlResultArray(sqlSelectResult));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
