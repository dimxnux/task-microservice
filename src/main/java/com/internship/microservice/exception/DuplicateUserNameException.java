package com.internship.microservice.exception;

public class DuplicateUserNameException extends RuntimeException {
    public DuplicateUserNameException() {
    }

    public DuplicateUserNameException(String message) {
        super(message);
    }
}
