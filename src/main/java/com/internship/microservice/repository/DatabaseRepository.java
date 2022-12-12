package com.internship.microservice.repository;

import com.internship.microservice.model.Database;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface DatabaseRepository extends JpaRepository<Database, String> {
    Optional<Database> findByName(String name);
    @Transactional
    void deleteByName(String name);
}
