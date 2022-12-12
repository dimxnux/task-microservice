package com.internship.microservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                               HttpServletRequest request) {
        log.error("Exception while handling request: ", e);
        BindingResult bindingResult = e.getBindingResult();
        List<String> errorMessages = new ArrayList<>();

        for (ObjectError error : bindingResult.getAllErrors()) {
            String resolvedMessage = messageSource.getMessage(error, Locale.US);
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errorMessages.add(String.format("Field '%s' %s but value was '%s'", fieldError.getField(), resolvedMessage,
                        fieldError.getRejectedValue()));
            } else {
                errorMessages.add(resolvedMessage);
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, request.getServletPath());

        return ResponseEntity.badRequest().
                body(errorResponse);
    }

    @ExceptionHandler(value = {DuplicateDatabaseNameException.class, DatabaseNotFoundException.class})
    public ResponseEntity<ErrorResponse> onDuplicateDatabaseAliasException(RuntimeException e,
                                                                           HttpServletRequest request) {
        log.error("Exception while handling request: ", e);
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, request.getServletPath());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> onConstraintViolationException(ConstraintViolationException e,
                                                                        HttpServletRequest request) {
        log.error("Exception while handling request: ", e);
        List<String> errorMessages = new ArrayList<>();

        e.getConstraintViolations().forEach(violation -> {
            errorMessages.add(String.format("'%s' %s but value was %s", getInvalidPropertyName(violation),
                            violation.getMessage(), violation.getInvalidValue()));
        });

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, request.getServletPath());

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    private String getInvalidPropertyName(ConstraintViolation<?> violation) {
        return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                .map(Path.Node::getName)
                .reduce((a, b) -> b)
                .orElse(violation.getPropertyPath().toString());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
                                                                               HttpServletRequest request) {
        log.error("Exception while handling request: ", e);
        List<String> errorMessages = new ArrayList<>();

        MethodParameter parameter = e.getParameter();
        String errorMessage = String.format("Invalid value for the parameter '%s'. Value '%s' could not be bound to type '%s'",
                parameter.getParameterName(), e.getValue(), parameter.getParameterType().getSimpleName());
        errorMessages.add(errorMessage);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, request.getServletPath());

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }
}
