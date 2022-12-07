package com.internship.microservice.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ErrorResponse {
    private final HttpStatus statusCode;
    private final List<String> messages;
    private final String path;

    public ErrorResponse(HttpStatus statusCode, List<String> messages, String path) {
        this.statusCode = statusCode;
        this.messages = messages;
        this.path = path;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getPath() {
        return path;
    }
}
