package com.internship.microservice.exception;

public class DuplicateDatabaseNameException extends RuntimeException {

    public DuplicateDatabaseNameException() {
    }

    public DuplicateDatabaseNameException(String message) {
        super(message);
    }
}
