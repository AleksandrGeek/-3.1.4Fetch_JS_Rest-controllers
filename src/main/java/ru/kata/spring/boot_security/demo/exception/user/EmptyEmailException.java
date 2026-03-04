package ru.kata.spring.boot_security.demo.exception.user;


public class EmptyEmailException extends RuntimeException {
    public EmptyEmailException() {
        super("Email cannot be empty");
    }
}
