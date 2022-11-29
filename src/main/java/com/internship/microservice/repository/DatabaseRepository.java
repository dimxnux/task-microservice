package com.internship.microservice.repository;

import com.internship.microservice.model.Database;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseRepository extends JpaRepository<Database, Long> {
}
