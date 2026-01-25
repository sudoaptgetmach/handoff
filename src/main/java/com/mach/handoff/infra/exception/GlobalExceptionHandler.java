package com.mach.handoff.infra.exception;

import com.mach.handoff.domain.exception.DomainException;
import com.mach.handoff.domain.exception.ForbiddenException;
import com.mach.handoff.domain.exception.NotFoundException;
import com.mach.handoff.domain.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> handleNotFound(NotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardError> handleForbidden(ForbiddenException e) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", e.getMessage());
    }

    @ExceptionHandler({DomainException.class, ValidationException.class})
    public ResponseEntity<StandardError> handleBusinessRules(RuntimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Business Rule Violation", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleJsonError() {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Corpo da requisição inválido ou mal formatado.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleBeanValidation(MethodArgumentNotValidException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ValidationError err = new ValidationError(
                Instant.now(),
                status.value(),
                "Input Validation Error",
                "Dados inválidos ou incompletos"
        );

        e.getBindingResult().getFieldErrors().forEach(f ->
                err.addError(f.getField(), f.getDefaultMessage())
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGlobal(Exception e) {
        e.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Erro interno no servidor.");
    }

    private ResponseEntity<StandardError> buildResponse(HttpStatus status, String error, String message) {
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                message
        );
        return ResponseEntity.status(status).body(err);
    }
}