package com.internship.microservice.repository;

import com.internship.microservice.model.Database;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DatabaseRepository extends JpaRepository<Database, Long> {
    Optional<Database> findByAlias(String alias);
    void deleteByAlias(String alias);
}
