package com.internship.microservice.repository;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class SourceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public String getCurrentDatabase() {
        return entityManager.createNativeQuery("SELECT current_database()")
                .getSingleResult()
                .toString();
    }
}
