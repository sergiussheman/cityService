package com.itechart.cityservice.controller;

import com.itechart.cityservice.dto.ExceptionDTO;
import com.itechart.cityservice.exception.PathNotExistsException;
import com.itechart.cityservice.exception.UnknownCityException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { UnknownCityException.class, PathNotExistsException.class })
    public ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        ExceptionDTO bodyOfResponse = new ExceptionDTO(ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<Object> handleConflict(ConstraintViolationException ex, WebRequest request) {
        ExceptionDTO bodyOfResponse = new ExceptionDTO(ex.getMessage());
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}