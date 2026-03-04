package ru.kata.spring.boot_security.demo.exception.user;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String email) {
        super("Email already registered: " + email);
    }
}
