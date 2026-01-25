package com.mach.handoff.infra.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

@Getter
public class ValidationError extends StandardError {
    private final List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer status, String error, String message) {
        super(timestamp, status, error, message);
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }

    public record FieldMessage(String field, String message) { }
}