package ru.kata.spring.boot_security.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.kata.spring.boot_security.demo.exception.role.RoleNotFoundException;
import ru.kata.spring.boot_security.demo.exception.user.DuplicatedEmailException;
import ru.kata.spring.boot_security.demo.exception.user.EmptyEmailException;
import ru.kata.spring.boot_security.demo.exception.user.UserNotFoundException;
import ru.kata.spring.boot_security.demo.exception.user.ValidationException;


import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerUserNotFound(
            UserNotFoundException e,
            HttpServletRequest request
    ) {
        log.warn("User not found:{}", e.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND,
                e.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ErrorResponse> handlerDuplicatedEmail(
            DuplicatedEmailException e,
            HttpServletRequest request
    ) {
        log.warn("Duplicate email:{}", e.getMessage());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                e.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler({EmptyEmailException.class, ValidationException.class})
    public ResponseEntity<ErrorResponse> handlerEmailAndValidation(
            RuntimeException e,
            HttpServletRequest request
    ) {
        log.warn("Validation error: {}", e.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request.getRequestURI()
        );
    }


    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerRoleNotFound(
            RoleNotFoundException e,
            HttpServletRequest request
    ) {
        log.warn("Role not found: {}", e.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        log.warn("Type mismatch for parameter: {}", e.getName());

        String requiredType = e.getRequiredType() != null
                ? e.getRequiredType().getSimpleName()
                : "unknown type";


        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid parameter format: " +
                        requiredType,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.warn("Unexpected error", e);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                request.getRequestURI()
        );
    }


    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path) {

        ErrorResponse error = new ErrorResponse(
                status.value(),
                message,
                path
        );

        return ResponseEntity.status(status).body(error);


    }

}
