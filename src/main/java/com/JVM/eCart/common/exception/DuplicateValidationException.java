package com.JVM.eCart.common.exception;

import java.util.Map;

public class DuplicateValidationException extends RuntimeException {
    public DuplicateValidationException(String message) {
        super(message);
    }

//    public DuplicateValidationException(Map<String, String> errors) {
//        super("Registration validation failed");
//        this.errors = errors;
//    }
//
//    private final Map<String, String> errors;
//
//    public Map<String, String> getErrors() {
//        return errors;
//    }
}
