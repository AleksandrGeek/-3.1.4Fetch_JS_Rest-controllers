package ru.kata.spring.boot_security.demo.exception.user;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
