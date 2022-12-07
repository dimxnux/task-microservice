package com.internship.microservice.exception;

public class DuplicateDatabaseAliasException extends RuntimeException {

    public DuplicateDatabaseAliasException() {
    }

    public DuplicateDatabaseAliasException(String message) {
        super(message);
    }
}
