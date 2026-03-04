package ru.kata.spring.boot_security.demo.exception;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    int status;
    String message;
    String path;
    LocalDateTime timestamp;

    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

}
