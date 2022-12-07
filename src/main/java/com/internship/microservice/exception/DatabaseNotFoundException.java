package com.internship.microservice.exception;

public class DatabaseNotFoundException extends RuntimeException {
    public DatabaseNotFoundException() {
    }

    public DatabaseNotFoundException(String message) {
        super(message);
    }
}
