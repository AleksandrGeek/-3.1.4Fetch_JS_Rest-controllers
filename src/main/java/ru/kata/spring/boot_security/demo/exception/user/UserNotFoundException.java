package ru.kata.spring.boot_security.demo.exception.user;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends RuntimeException {
   public UserNotFoundException(Long id) {
       super("User not found with id: " + id + " " + HttpStatus.NOT_FOUND);
   }
}
