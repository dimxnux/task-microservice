package com.internship.microservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
}
